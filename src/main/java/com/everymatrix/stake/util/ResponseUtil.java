package com.everymatrix.stake.util;

import java.io.IOException;
import java.io.OutputStream;
import com.everymatrix.stake.dto.ApiResult;
import com.everymatrix.stake.shared.Constant;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class ResponseUtil {

    private ResponseUtil() {}

    public static void withFail(HttpExchange exchange, int bizCode, String msg) throws IOException {
        ApiResult result = new ApiResult(bizCode, msg, null);
        String jsonResult = result.toString();
        exchange.getResponseHeaders().set("Content-Type", Constant.RETURN_JSON_TYPE);
        exchange.sendResponseHeaders(Constant.HTTP_BAD_REQUEST, jsonResult.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResult.getBytes());
        os.close();
    }

    public static <T> void withSuccess(HttpExchange exchange, int bizCode, T data) throws IOException {
        ApiResult<T> result = new ApiResult<>(bizCode, null, data);
        String jsonResult = result.toString();
        exchange.getResponseHeaders().set("Content-Type", Constant.RETURN_JSON_TYPE);
        exchange.sendResponseHeaders(Constant.HTTP_OK, jsonResult.length());
        OutputStream os = exchange.getResponseBody();
        os.write(jsonResult.getBytes());
        os.close();
    }
}
