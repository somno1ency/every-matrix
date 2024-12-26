package com.everymatrix.stake.strategy.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.everymatrix.stake.manager.StakeManager;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.strategy.RouterStrategy;
import com.everymatrix.stake.util.ResponseUtil;
import com.everymatrix.stake.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class PostStakeRouterStrategy implements RouterStrategy {

    private static final Pattern requiredPattern = Pattern.compile(Constant.POST_STAKE);

    @Override
    public boolean isMatch(String path, String method) {
        Matcher matcher = requiredPattern.matcher(path);
        return matcher.find() && Constant.HTTP_POST.equals(method);
    }

    @Override
    public void process(HttpExchange exchange, String path) throws IOException {
        Matcher matcher = requiredPattern.matcher(path);
        if (!matcher.find()) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "unexpected matcher err for post stake...");
            return;
        }
        int betOfferId;
        try {
            betOfferId = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "betOfferId verify err: " + e.getMessage());
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isEmpty()) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "required param sessionkey in query not found...");
            return;
        }
        Map<String, String> queryMap = StringUtil.queryAsMap(query);
        if (!queryMap.containsKey("sessionkey")) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "required param sessionkey in query not found....");
            return;
        }
        if (exchange.getRequestBody().available() == 0) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "required body param for stake not found...");
            return;
        }
        String stakeStr = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (stakeStr.isBlank()) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "miss field stake in body param...");
            return;
        }
        int stake;
        try {
            stake = Integer.parseInt(stakeStr);
        } catch (NumberFormatException e) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "body stake verify err: " + e.getMessage());
            return;
        }

        StakeManager.postStake(exchange, betOfferId, queryMap.get("sessionkey"), stake);
    }
}
