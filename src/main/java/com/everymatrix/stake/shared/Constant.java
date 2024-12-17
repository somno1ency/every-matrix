package com.everymatrix.stake.shared;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public interface Constant {

    /**
     * 1.http method definitions
     */
    String HTTP_GET = "GET";

    String HTTP_POST = "POST";

    /**
     * 2.router definitions
     */
    String GET_SESSION = "(\\d+)/session$";

    String GET_HIGH_STAKES = "(\\d+)/highstakes$";

    String POST_STAKE = "(\\d+)/stake$";

    /**
     * 3.http status code definitions
     */
    int HTTP_BAD_REQUEST = 400;

    int HTTP_OK = 200;

    /**
     * 5.content type definitions
     */
    String RETURN_TEXT_TYPE = "text/plain";

    String RETURN_OCTET_TYPE = "application/octet-stream";

    /**
     * 6.others
     */
    int SESSION_EXPIRE_TIME = 10 * 60 * 1000;

    String STAKE_INFO = "%d:%d";
}
