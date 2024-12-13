package com.everymatrix.stake.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * for setting name of threads
 *
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class NamingThreadFactory implements ThreadFactory {

    private final AtomicInteger threadIdentity = new AtomicInteger();

    private final String prefix;

    public NamingThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prefix + threadIdentity.incrementAndGet());
    }
}
