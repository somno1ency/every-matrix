package com.everymatrix.stake.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class SessionResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sessionKey;

    public SessionResp(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public String toString() {
        return String.format("""
            {
                "sessionKey": %s
            }
            """, this.sessionKey);
    }
}
