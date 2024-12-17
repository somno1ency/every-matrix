package com.everymatrix.stake.model;

/**
 * @author mackay.zhou
 * created at 2024/12/12
 */
public class StakeInfo {

    private int customerId;

    private int stake;

    public StakeInfo(int customerId, int stake) {
        this.customerId = customerId;
        this.stake = stake;
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
}
