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
package com.firenio.component;

import com.firenio.buffer.ByteBuf;

/**
 * @program: firenio
 * @description:
 * @author: wangkai
 * @create: 2019-05-01 21:48
 **/
public class Test {

    public static void main(String[] args) {

        final String[] ss      = new String[5];
        final int      max_len = ss.length;
        ss[0] = "11";
        ss[1] = "22";
        // 这里有可能是因为异常关闭，currentWriteFrameLen不准确
        // 对所有不为空的frame release
        String s = ss[0];
        for (int i = 0; i < max_len && s != null; s = ss[i]) {
            System.out.println(s);
            ss[i++] = null;
        }


    }


}
