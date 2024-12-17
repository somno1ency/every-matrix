package com.everymatrix.stake.util;

import java.io.IOException;
import java.io.OutputStream;
import com.everymatrix.stake.shared.Constant;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author mackay.zhou
 * created at 2024/12/11
 */
public class ResponseUtil {

    private ResponseUtil() {}

    public static void toResp(HttpExchange exchange, int httpCode, String data) throws IOException {
        if (data == null) {
            data = "";
        }
        exchange.getResponseHeaders().set("Content-Type", Constant.RETURN_TEXT_TYPE);
        exchange.sendResponseHeaders(httpCode, data.length());
        OutputStream os = exchange.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }
}