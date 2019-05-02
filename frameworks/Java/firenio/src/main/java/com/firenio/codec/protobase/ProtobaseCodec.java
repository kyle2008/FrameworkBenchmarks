/*
 * Copyright 2015 The FireNio Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firenio.codec.protobase;

import java.io.IOException;

import com.firenio.buffer.ByteBuf;
import com.firenio.component.Channel;
import com.firenio.component.Frame;
import com.firenio.component.ProtocolCodec;

/**
 * <pre>
 *  B0    : 0-1 : 0:tunnel,   1:broadcast, 2:ping, 3:pong
 *  B0    : 2   : 0:text,     1:binary
 *  B0    : 3   : 0:continue, 1:last
 *  B0    : 4-7 : ExtType
 *  B1-B3 : FrameLen
 *  B4-B7 : FrameId
 *  B8-B11: ChannelId
 *
 * </pre>
 */
public final class ProtobaseCodec extends ProtocolCodec {

    public static final IOException ILLEGAL_PROTOCOL   = EXCEPTION("illegal protocol");
    public static final IOException OVER_LIMIT         = EXCEPTION("over limit");
    static final        ByteBuf     PING;
    static final        ByteBuf     PONG;
    static final        int         PROTOCOL_HEADER    = 12;
    static final        int         PROTOCOL_PING      = 0b1000_0000;
    static final        int         PROTOCOL_PONG      = 0b1100_0000;
    static final        int         PROTOCOL_PONG_MASK = 0b1100_0000;

    static {
        PING = ByteBuf.direct(4);
        PONG = ByteBuf.direct(4);
        PING.putInt(PROTOCOL_PING << 24);
        PONG.putInt(PROTOCOL_PONG << 24);
        PING.flip();
        PONG.flip();
    }

    private final int limit;

    public ProtobaseCodec() {
        this(1024 * 64);
    }

    public ProtobaseCodec(int limit) {
        this.limit = limit;
    }

    @Override
    public Frame decode(Channel ch, ByteBuf src) throws IOException {
        if (src.remaining() < 4) {
            return null;
        }
        byte flags = src.absByte(src.absPos());
        int  len   = src.getInt() & 0xffffff;
        if (flags < 0) {
            return decode_ping(ch, flags);
        }
        if (len > limit) {
            throw OVER_LIMIT;
        }
        if (len > src.remaining()) {
            src.skip(-4);
            return null;
        }
        int    frameId   = src.getInt();
        int    channelId = src.getInt();
        byte[] data      = new byte[len - 8];
        src.getBytes(data);
        ProtobaseFrame f = new ProtobaseFrame();
        f.setFlags(flags);
        f.setChannelId(channelId);
        f.setFrameId(frameId);
        if (f.isText()) {
            f.setContent(new String(data, ch.getCharset()));
        } else {
            f.setContent(data);
        }
        return f;
    }

    private Frame decode_ping(Channel ch, int type) throws IOException {
        type &= PROTOCOL_PONG_MASK;
        if (type == PROTOCOL_PING) {
            log_ping_from(ch);
            flush_pong(ch, PONG.duplicate());
        } else if (type == PROTOCOL_PONG) {
            log_pong_from(ch);
        } else {
            throw ILLEGAL_PROTOCOL;
        }
        return null;
    }

    @Override
    protected ByteBuf getPingBuf() {
        return PING.duplicate();
    }

    @Override
    public ByteBuf encode(Channel ch, Frame frame) {
        ProtobaseFrame f   = (ProtobaseFrame) frame;
        ByteBuf        buf = f.getBufContent().flip();
        buf.putInt(0, (buf.limit() - 4) | (f.getFlags() << 24));
        buf.putInt(4, f.getFrameId());
        buf.putInt(8, f.getChannelId());
        return buf;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String getProtocolId() {
        return "Protobase";
    }

    @Override
    public int getHeaderLength() {
        return PROTOCOL_HEADER;
    }

}
