package com.everymatrix.stake.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import static com.everymatrix.stake.manager.SessionManager.customerToSessionCache;
import com.everymatrix.stake.model.StakeInfo;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.util.ResponseUtil;
import com.everymatrix.stake.util.TimeUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/17
 */
public class StakeManager {

    /**
     * we use betOfferId as key, all stakes for this betting as value, so value can use List or LinkedHashMap, for save memory, we just use List, but it needs more continuous memory
     */
    private static final HashMap<Integer, List<StakeInfo>> bettingMap = new HashMap<>();

    public static void postStake(HttpExchange exchange, int betOfferId, String sessionKey, int stake) throws IOException {
        Integer customerId = customerToSessionCache.getKeyByValue(sessionKey);
        if (customerId == null || customerId <= 0) {
            ResponseUtil.toResp(exchange, Constant.HTTP_BAD_REQUEST, "the sessionKey you use is invalid or expired: " + sessionKey);
            return;
        }
        StakeInfo info = new StakeInfo(customerId, stake, System.currentTimeMillis());
        synchronized (bettingMap) {
            List<StakeInfo> currentBetting = bettingMap.get(betOfferId);
            if (currentBetting == null) {
                bettingMap.put(betOfferId, new ArrayList<>(List.of(info)));
            } else {
                currentBetting.add(info);
            }
        }

        ResponseUtil.toResp(exchange, Constant.HTTP_OK, null);
    }

    public static void getHighStakes(HttpExchange exchange, int betOfferId) throws IOException {
        List<StakeInfo> stakeList = new ArrayList<>();
        List<StakeInfo> copyedList;
        List<Integer> customerIdList = new ArrayList<>();
        synchronized (bettingMap) {
            List<StakeInfo> currentBetting = bettingMap.get(betOfferId);
            if (currentBetting != null && !currentBetting.isEmpty()) {
                // why copy this array?
                // because we need to clean part of each slot of bettingMap, to improve efficiency we use timestamp to do the judgement, so we can't resort the array itself
                // PAY ATTENTION: will cost more memory
                copyedList = new ArrayList<>(currentBetting);
                copyedList.sort(Comparator.comparingInt(StakeInfo::getStake).reversed());
                for (StakeInfo stake : copyedList) {
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

    public static void cleanBeforeToday() {
        Iterator<List<StakeInfo>> iterator = bettingMap.values().iterator();
        synchronized (bettingMap) {
            while (iterator.hasNext()) {
                List<StakeInfo> current = iterator.next();
                if (current != null && !current.isEmpty()) {
                    Iterator<StakeInfo> stakeIterator = current.iterator();
                    while (stakeIterator.hasNext()) {
                        StakeInfo info = stakeIterator.next();
                        if (info != null && TimeUtil.isBeforeToday(info.getTimestamp())) {
                            stakeIterator.remove();
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
}
