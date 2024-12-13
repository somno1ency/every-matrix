package com.everymatrix.stake.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private T data;

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("""
            {
                "code": %d,
                "msg": %s,
                "data": %s,
            }
            """, this.code, this.msg, this.data);
    }
}
