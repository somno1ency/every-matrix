package com.everymatrix.stake.model;

/**
 * @author mackay.zhou
 * created at 2024/12/12
 */
public class StakeInfo {

    private Integer customerId;

    private Integer betOfferId;

    private Integer stake;

    public StakeInfo(Integer customerId, Integer betOfferId, Integer stake) {
        this.customerId = customerId;
        this.betOfferId = betOfferId;
        this.stake = stake;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getBetOfferId() {
        return betOfferId;
    }

    public void setBetOfferId(Integer betOfferId) {
        this.betOfferId = betOfferId;
    }

    public Integer getStake() {
        return stake;
    }

    public void setStake(Integer stake) {
        this.stake = stake;
    }
}
