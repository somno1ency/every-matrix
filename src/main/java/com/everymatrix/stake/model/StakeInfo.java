package com.everymatrix.stake.model;

/**
 * @author mackay.zhou
 * created at 2024/12/12
 */
public class StakeInfo {

    private int customerId;

    private int stake;

    private long timestamp;

    public StakeInfo(int customerId, int stake, long timestamp) {
        this.customerId = customerId;
        this.stake = stake;
        this.timestamp = timestamp;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStake() {
        return stake;
    }

    public void setStake(int stake) {
        this.stake = stake;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
