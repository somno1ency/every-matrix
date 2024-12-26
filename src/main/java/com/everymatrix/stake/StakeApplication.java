package com.everymatrix.stake;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.everymatrix.stake.handler.DispatcherHandler;
import com.everymatrix.stake.job.SessionCleaner;
import com.everymatrix.stake.job.StakeCleaner;
import com.sun.net.httpserver.HttpServer;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public class StakeApplication {

    public static void main(String[] args) {
        int port = 8001;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new DispatcherHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("web server started on port: " + port + ", visit url: http://127.0.0.1:" + port);

        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
        scheduled.scheduleWithFixedDelay(SessionCleaner::clean, 0, 5, TimeUnit.SECONDS);
        scheduled.scheduleWithFixedDelay(StakeCleaner::clean, 0, 24, TimeUnit.HOURS);
    }
}
