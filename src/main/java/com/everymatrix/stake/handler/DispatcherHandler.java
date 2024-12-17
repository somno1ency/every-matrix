package com.everymatrix.stake.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.everymatrix.stake.manager.SessionManager;
import com.everymatrix.stake.manager.StakeManager;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.util.ResponseUtil;
import com.everymatrix.stake.util.StringUtil;
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
        Pattern getSessionPattern = Pattern.compile(Constant.GET_SESSION);
        Pattern getStakesPattern = Pattern.compile(Constant.GET_HIGH_STAKES);
        Pattern postStakePattern = Pattern.compile(Constant.POST_STAKE);
        Matcher getSessionMatcher = getSessionPattern.matcher(path);
        Matcher getStakesMatcher = getStakesPattern.matcher(path);
        Matcher postStakeMatcher = postStakePattern.matcher(path);
        // match router and process for corresponding router
        if (getSessionMatcher.find() && Constant.HTTP_GET.equals(method)) {
            String customerIdStr = getSessionMatcher.group(1);
            int customerId;
            try {
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "customerId verify err: " + e.getMessage());
                return;
            }
            ResponseUtil.toResp(exchange, Constant.HTTP_OK, SessionManager.getSession(customerId));
        } else if (getStakesMatcher.find() && Constant.HTTP_GET.equals(method)) {
            String betOfferIdStr = getStakesMatcher.group(1);
            int betOfferId;
            try {
                betOfferId = Integer.parseInt(betOfferIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "betOfferId verify err: " + e.getMessage());
                return;
            }
            StakeManager.getHighStakes(exchange, betOfferId);
        } else if (postStakeMatcher.find() && Constant.HTTP_POST.equals(method)) {
            String betOfferIdStr = postStakeMatcher.group(1);
            int betOfferId;
            try {
                betOfferId = Integer.parseInt(betOfferIdStr);
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
        } else {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "the api you request is not found: " + method + ":" + path);
        }
    }
}
