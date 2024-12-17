package com.everymatrix.stake.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import static com.everymatrix.stake.manager.SessionManager.customerToSessionCache;
import com.everymatrix.stake.model.StakeInfo;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.util.ResponseUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/17
 */
public class StakeManager {

    /**
     * we use betOfferId as key, all stakes for this betting as value, so value can use List or LinkedHashMap, for save memory, we just use List, but it needs more continuous memory
     */
    private static final ConcurrentHashMap<Integer, List<String>> bettingMap = new ConcurrentHashMap<>();

    public static void postStake(HttpExchange exchange, int betOfferId, String sessionKey, int stake) throws IOException {
        Integer customerId = customerToSessionCache.getKeyByValue(sessionKey);
        if (customerId == null || customerId <= 0) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "the sessionKey you use is invalid or expired: " + sessionKey);
            return;
        }
        String stakeStr = String.format(Constant.STAKE_INFO, customerId, stake);
        synchronized (bettingMap) {
            List<String> currentBetting = bettingMap.get(betOfferId);
            if (currentBetting == null) {
                bettingMap.put(betOfferId, new ArrayList<>(List.of(stakeStr)));
            } else {
                if (!currentBetting.contains(stakeStr)) {
                    currentBetting.add(stakeStr);
                }
            }
        }

        ResponseUtil.toResp(exchange, Constant.HTTP_OK, null);
    }

    public static void getHighStakes(HttpExchange exchange, int betOfferId) throws IOException {
        List<StakeInfo> stakeList = new ArrayList<>();
        List<Integer> customerIdList = new ArrayList<>();
        synchronized (bettingMap) {
            List<String> currentBetting = bettingMap.get(betOfferId);
            if (currentBetting != null && !currentBetting.isEmpty()) {
                List<StakeInfo> result = parseFromStr(currentBetting);
                if (result != null && !result.isEmpty()) {
                    result.sort(Comparator.comparingInt(StakeInfo::getStake).reversed());
                    for (StakeInfo stake : result) {
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
            }
        }

        // write csv
        String filename = "data.csv";
        int stakeSize = stakeList.size();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < stakeSize; i++) {
                String content = stakeList.get(i).getCustomerId() + "=" + stakeList.get(i).getStake();
                if (i == stakeSize - 1) {
                    writer.write(content);
                } else {
                    writer.write(content + ",");
                }
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

    private static List<StakeInfo> parseFromStr(List<String> stakeStrList) {
        List<StakeInfo> result = new ArrayList<>();
        if (stakeStrList == null || stakeStrList.isEmpty()) {
            return result;
        }

        for (String stakeStr : stakeStrList) {
            String[] pair = stakeStr.split(":");
            if (pair.length == 2) {
                int customerId;
                int stake;
                try {
                    customerId = Integer.parseInt(pair[0]);
                    stake = Integer.parseInt(pair[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return result;
                }
                result.add(new StakeInfo(customerId, stake));
            }
        }

        return result;
    }
}
