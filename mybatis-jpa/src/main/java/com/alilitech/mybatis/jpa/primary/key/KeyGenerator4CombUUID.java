/*
 *    Copyright 2017-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.alilitech.mybatis.jpa.primary.key;

import com.alilitech.mybatis.jpa.parameter.GenerationType;

import java.security.SecureRandom;
import java.util.SplittableRandom;
import java.util.UUID;

/**
 * primary key generator of {@link GenerationType#COMB_UUID}
 *
 * @author Zhou Xiaoxiang
 */
public class KeyGenerator4CombUUID implements KeyGenerator {

    private static final ThreadLocal<SplittableRandom> SPLITTABLE_RANDOM_THREAD_LOCAL = new ThreadLocal<SplittableRandom>() {

        private final SplittableRandom random = new SplittableRandom(new SecureRandom().nextLong());

        @Override
        protected SplittableRandom initialValue() {
            synchronized (random) {
                return random.split();
            }
        }
    };

    @Override
    public Object generate(Object entity) {
        SplittableRandom random = SPLITTABLE_RANDOM_THREAD_LOCAL.get();

        long hiBits = random.nextLong();
        long loBits = random.nextLong();

        hiBits = (System.currentTimeMillis() << 16) | (hiBits & 0x000000000000FFFF);

        return new UUID(hiBits, loBits).toString().replace("-", "");
    }
}
