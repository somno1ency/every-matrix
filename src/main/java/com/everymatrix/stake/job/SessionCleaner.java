package com.everymatrix.stake.job;

import com.everymatrix.stake.manager.SessionManager;

/**
 * @author mackay.zhou
 * created at 2024/12/17
 */
public class SessionCleaner {

    public static void clean() {
        SessionManager.customerToSessionCache.cleanExpired();
    }
}
