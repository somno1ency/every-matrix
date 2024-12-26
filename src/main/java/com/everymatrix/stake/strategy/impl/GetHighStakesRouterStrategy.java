package com.everymatrix.stake.strategy.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.everymatrix.stake.manager.StakeManager;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.strategy.RouterStrategy;
import com.everymatrix.stake.util.ResponseUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class GetHighStakesRouterStrategy implements RouterStrategy {

    private static final Pattern requiredPattern = Pattern.compile(Constant.GET_HIGH_STAKES);

    @Override
    public boolean isMatch(String path, String method) {
        Matcher matcher = requiredPattern.matcher(path);
        return matcher.find() && Constant.HTTP_GET.equals(method);
    }

    @Override
    public void process(HttpExchange exchange, String path) throws IOException {
        // why match again rather than make the variable matcher as a class member so that didn't need to transfer "path" in process?
        // because all strategy in whole application only exist one instance(load by ServiceLoader only once), so if it's a class member, when multiple threads
        // call isMatch with different path, the matcher will be modified, it's not safe if we didn't lock it, but here lock is not a good idea, so give this
        // method the "path" again and rematch it
        Matcher matcher = requiredPattern.matcher(path);
        if (!matcher.find()) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "unexpected matcher err for get high stakes...");
            return;
        }
        int betOfferId;
        try {
            betOfferId = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "betOfferId verify err: " + e.getMessage());
            return;
        }

        StakeManager.getHighStakes(exchange, betOfferId);
    }
}
