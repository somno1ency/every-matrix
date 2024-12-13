package com.everymatrix.stake;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.everymatrix.stake.handler.DispatcherHandler;
import com.everymatrix.stake.pool.ThreadPoolInitializer;
import com.sun.net.httpserver.HttpServer;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public class StakeApplication {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/app/v1", new DispatcherHandler());
            server.setExecutor(ThreadPoolInitializer.getHttpPool());
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
