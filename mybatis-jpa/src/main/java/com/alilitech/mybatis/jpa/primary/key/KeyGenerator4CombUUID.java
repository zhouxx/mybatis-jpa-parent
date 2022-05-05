package com.alilitech.mybatis.jpa.primary.key;

import java.security.SecureRandom;
import java.util.SplittableRandom;
import java.util.UUID;

public class KeyGenerator4CombUUID implements KeyGenerator {

    private static final ThreadLocal<SplittableRandom> splittableRandomThreadLocal = new ThreadLocal<SplittableRandom>() {

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
        SplittableRandom random = splittableRandomThreadLocal.get();

        long hiBits = random.nextLong();
        long loBits = random.nextLong();

        hiBits = (System.currentTimeMillis() << 16) | (hiBits & 0x000000000000FFFF);

        return new UUID(hiBits, loBits).toString().replace("-", "");
    }
}
