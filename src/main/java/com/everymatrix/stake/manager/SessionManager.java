package com.everymatrix.stake.manager;

import com.everymatrix.stake.cache.BidirectionalCache;
import com.everymatrix.stake.shared.Constant;
import com.everymatrix.stake.util.StringUtil;

/**
 * @author mackay.zhou
 * created at 2024/12/17
 */
public class SessionManager {

    /**
     * for store the mapping relationship of customerId and sessionKey
     */
    public static final BidirectionalCache<Integer, String> customerToSessionCache = new BidirectionalCache<>(Constant.SESSION_EXPIRE_TIME);

    public static String getSession(int customerId) {
        String uniqueId = customerToSessionCache.getValueByKey(customerId);
        if (uniqueId == null) {
            uniqueId = StringUtil.genUniqueId(10);
            customerToSessionCache.put(customerId, uniqueId);
        }

        return uniqueId;
    }
}
