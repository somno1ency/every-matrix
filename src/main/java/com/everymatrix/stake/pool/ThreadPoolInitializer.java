package com.everymatrix.stake.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.everymatrix.stake.shared.Constant;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public class ThreadPoolInitializer {

    private static boolean isInitializedForHttp = false;

    private static boolean isIsInitializedForWork = false;

    private static ExecutorService httpPool;

    private static ExecutorService workPool;

    /**
     * http thread pool setting by main in StakeApplication, so there is no multiple threads problem, we can just create it simply
     *
     * it's a kind of io intensive pool, it just used to retrieve info from route and forward http request, so we can set the max size to a larger value that far exceeding the cpu core size
     * to support larger concurrency
     */
    public static ExecutorService getHttpPool() {
        if (!isInitializedForHttp) {
            httpPool = new ThreadPoolExecutor(Constant.HTTP_POOL_CORE_SIZE, Constant.HTTP_POOL_MAX_SIZE, Constant.THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(Constant.HTTP_POOL_QUEUE_SIZE), new NamingThreadFactory(Constant.HTTP_POOL_PREFIX), new ThreadPoolExecutor.CallerRunsPolicy());
            isInitializedForHttp = true;
        }

        return httpPool;
    }

    /**
     * work pool may be used for multiple threads scene, so we need to lock when create it
     *
     * it's a kind of cpu intensive pool, every thread may do heavy tasks, so the core size and max size need to be set close to cpu core size
     */
    public static synchronized ExecutorService getWorkPool() {
        if (!isIsInitializedForWork) {
            workPool = new ThreadPoolExecutor(Constant.WORK_POOL_CORE_SIZE, Constant.WORK_POOL_MAX_SIZE, Constant.THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(Constant.WORK_POOL_QUEUE_SIZE), new NamingThreadFactory(Constant.WORK_POOL_PREFIX), new ThreadPoolExecutor.CallerRunsPolicy());
            isIsInitializedForWork = true;
        }

        return workPool;
    }
}
