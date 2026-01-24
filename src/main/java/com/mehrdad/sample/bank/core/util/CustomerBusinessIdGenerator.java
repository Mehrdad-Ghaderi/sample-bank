package com.mehrdad.sample.bank.core.util;

import java.util.concurrent.atomic.AtomicInteger;

public class CustomerBusinessIdGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(100000);

    private CustomerBusinessIdGenerator() {} // utility class

    public static Integer generate() {
        return sequence.getAndIncrement(); // auto-increment safely
    }

}
