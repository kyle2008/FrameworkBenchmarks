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
package com.firenio;

import com.firenio.log.Logger;

/**
 * @author wangkai
 */
public class Develop {

    public static final boolean BUF_DEBUG         = DevelopConfig.BUF_DEBUG;
    public static final boolean EPOLL_DEBUG       = DevelopConfig.EPOLL_DEBUG;
    public static final String  EPOLL_PATH        = DevelopConfig.EPOLL_PATH;
    public static final boolean NATIVE_DEBUG      = DevelopConfig.NATIVE_DEBUG;
    public static final int     DEBUG_ERROR_LEVEL = Options.getDebugErrorLevel();

    public static void printException(Logger logger, Throwable e, int level) {
        if (level > DEBUG_ERROR_LEVEL) {
            logger.error(e.getMessage(), e);
        } else {
            logger.debug(e.getMessage(), e);
        }
    }

}
