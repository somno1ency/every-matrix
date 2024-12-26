package com.everymatrix.stake.job;

import com.everymatrix.stake.manager.StakeManager;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class StakeCleaner {

    public static void clean() {
        StakeManager.cleanBeforeToday();
    }
}
