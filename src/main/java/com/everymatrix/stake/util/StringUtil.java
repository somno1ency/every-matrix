package com.everymatrix.stake.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class StringUtil {

    private static final String LETTERS = "abcdefhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * it's a kind of tool, so make the constructor as private to ensure anywhere can't new it
     */
    private StringUtil() {}

    public static Map<String, String> queryAsMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null && !query.isBlank()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    result.put(parts[0], parts[1]);
                }
            }
        }

        return result;
    }

    public static String genUniqueId(int length) {
        long timestamp = System.currentTimeMillis();
        int lettersLength = LETTERS.length();
        Random random = new Random(timestamp);
        StringBuilder uniqueId = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(lettersLength);
            uniqueId.append(LETTERS.charAt(index));
        }

        return uniqueId.toString();
    }
}
