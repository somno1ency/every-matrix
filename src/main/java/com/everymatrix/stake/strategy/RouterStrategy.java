package com.everymatrix.stake.strategy;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public interface RouterStrategy {

    boolean isMatch(String path, String method);

    void process(HttpExchange exchange, String path) throws IOException;
}
