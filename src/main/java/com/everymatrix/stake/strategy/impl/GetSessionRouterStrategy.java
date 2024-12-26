package com.everymatrix.stake.strategy.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.everymatrix.stake.manager.SessionManager;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.strategy.RouterStrategy;
import com.everymatrix.stake.util.ResponseUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class GetSessionRouterStrategy implements RouterStrategy {

    private static final Pattern requiredPattern = Pattern.compile(Constant.GET_SESSION);

    @Override
    public boolean isMatch(String path, String method) {
        Matcher matcher = requiredPattern.matcher(path);
        return matcher.find() && Constant.HTTP_GET.equals(method);
    }

    @Override
    public void process(HttpExchange exchange, String path) throws IOException {
        Matcher matcher = requiredPattern.matcher(path);
        if (!matcher.find()) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "unexpected matcher err for get session...");
            return;
        }
        int customerId;
        try {
            customerId = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "customerId verify err: " + e.getMessage());
            return;
        }

        ResponseUtil.toResp(exchange, Constant.HTTP_OK, SessionManager.getSession(customerId));
    }
}
