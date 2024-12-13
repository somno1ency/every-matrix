package com.everymatrix.stake.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.everymatrix.stake.cache.BidirectionalCache;
import com.everymatrix.stake.dto.SessionResp;
import com.everymatrix.stake.model.StakeInfo;
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

    /**
     * for store the mapping relationship of customerId and sessionKey
     */
    private static final BidirectionalCache<Integer, String> customerToSessionCache = new BidirectionalCache<>(10 * 60 * 1000);

    /**
     * we use betOfferId as key, all stakes for this betting as value, so value can use List or LinkedHashMap, for save memory, we just use List, but it needs more continuous memory
     */
    private static final ConcurrentHashMap<Integer, List<StakeInfo>> bettingMap = new ConcurrentHashMap<>();

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
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "customerId verify err: " + e.getMessage());
                return;
            }
            ResponseUtil.withSuccess(exchange, Constant.BIZ_NORMAL, getSession(customerId));
        } else if (getStakesMatcher.find() && Constant.HTTP_GET.equals(method)) {
            String betOfferIdStr = getStakesMatcher.group(1);
            int betOfferId;
            try {
                betOfferId = Integer.parseInt(betOfferIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "betOfferId verify err: " + e.getMessage());
                return;
            }
            getHighStakes(exchange, betOfferId);
        } else if (postStakeMatcher.find() && Constant.HTTP_POST.equals(method)) {
            String betOfferIdStr = postStakeMatcher.group(1);
            int betOfferId;
            try {
                betOfferId = Integer.parseInt(betOfferIdStr);
            } catch (NumberFormatException e) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "betOfferId verify err: " + e.getMessage());
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            if (query == null || query.isEmpty()) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "required param sessionKey in query not found...");
                return;
            }
            Map<String, String> queryMap = StringUtil.queryAsMap(query);
            if (!queryMap.containsKey("sessionKey")) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "required param sessionKey in query not found....");
                return;
            }
            if (exchange.getRequestBody().available() == 0) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "required body param for stake not found...");
                return;
            }
            String stakeStr = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (stakeStr.isBlank()) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "miss field stake in body param...");
                return;
            }
            int stake;
            try {
                stake = Integer.parseInt(stakeStr);
            } catch (NumberFormatException e) {
                ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "body stake verify err: " + e.getMessage());
                return;
            }

            postStake(exchange, betOfferId, queryMap.get("sessionKey"), stake);
            ResponseUtil.withSuccess(exchange, Constant.BIZ_NORMAL, null);
        } else {
            ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "the api you request is not found: " + method + ":" + path);
        }
    }

    private SessionResp getSession(int customerId) {
        String uniqueId = customerToSessionCache.getValueByKey(customerId);
        if (uniqueId == null) {
            uniqueId = StringUtil.genUniqueId(10);
            customerToSessionCache.put(customerId, uniqueId);
        }

        return new SessionResp(uniqueId);
    }

    private void getHighStakes(HttpExchange exchange, int betOfferId) throws IOException {
        List<StakeInfo> currentBetting = bettingMap.get(betOfferId);
        List<StakeInfo> stakeList = new ArrayList<>();
        if (currentBetting != null && !currentBetting.isEmpty()) {
            currentBetting.sort(Comparator.comparingInt(StakeInfo::getStake).reversed());
            List<Integer> customerIdList = new ArrayList<>();
            for (StakeInfo stake : currentBetting) {
                if (customerIdList.contains(stake.getCustomerId())) {
                    continue;
                }

                stakeList.add(stake);
                customerIdList.add(stake.getCustomerId());
                if (stakeList.size() == 20) {
                    break;
                }
            }
        }

        // write csv
        String filename = "data.csv";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (StakeInfo stake : stakeList) {
                String line = stake.getCustomerId() + "=" + stake.getStake();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // send file to frontend
        File csvFile = new File(filename);
        exchange.getResponseHeaders().set("Content-Type", Constant.RETURN_OCTET_TYPE);
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=" + filename);
        exchange.sendResponseHeaders(Constant.HTTP_OK, csvFile.length());

        FileInputStream fis = new FileInputStream(csvFile);
        OutputStream os = exchange.getResponseBody();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        fis.close();
        os.close();
    }

    private void postStake(HttpExchange exchange, int betOfferId, String sessionKey, int stake) throws IOException {
        Integer customerId = customerToSessionCache.getKeyByValue(sessionKey);
        if (customerId == null || customerId <= 0) {
            ResponseUtil.withFail(exchange, Constant.BIZ_SYSTEM_ERROR, "the sessionKey you use is invalid or expired: " + sessionKey);
            return;
        }
        StakeInfo stakeInfo = new StakeInfo(customerId, betOfferId, stake);
        List<StakeInfo> currentBetting = bettingMap.get(betOfferId);
        if (currentBetting == null) {
            bettingMap.put(betOfferId, new ArrayList<>(List.of(stakeInfo)));
        } else {
            currentBetting.add(stakeInfo);
        }
    }
}
