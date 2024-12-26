package com.everymatrix.stake.handler;

import java.io.IOException;
import com.everymatrix.stake.context.RouterContext;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.strategy.RouterStrategy;
import com.everymatrix.stake.util.ResponseUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public class DispatcherHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        RouterStrategy strategy = RouterContext.getStrategy(path, method);
        if (strategy == null) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "the api you request is not found: " + method + ":" + path);
            return;
        }

        strategy.process(exchange, path);
    }
}
