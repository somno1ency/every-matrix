package com.everymatrix.stake.shared;

/**
 * @author mackay.zhou
 * created at 2024/12/9
 */
public interface Constant {

    /**
     * 1.thread pool constant definitions
     */
    int HTTP_POOL_CORE_SIZE = 4;

    int HTTP_POOL_MAX_SIZE = 8;

    int HTTP_POOL_QUEUE_SIZE = 500;

    String HTTP_POOL_PREFIX = "http-pool-";

    int WORK_POOL_CORE_SIZE = 4;

    int WORK_POOL_MAX_SIZE = 8;

    int WORK_POOL_QUEUE_SIZE = 300;

    String WORK_POOL_PREFIX = "work-pool-";

    int THREAD_KEEP_ALIVE_TIME = 60;

    /**
     * 2.http method definitions
     */
    String HTTP_GET = "GET";

    String HTTP_POST = "POST";

    /**
     * 3.router definitions
     */
    String GET_SESSION = "(\\d+)/session$";

    String GET_HIGH_STAKES = "(\\d+)/highstakes$";

    String POST_STAKE = "(\\d+)/stake$";

    /**
     * 4.http status code definitions
     */
    int HTTP_BAD_REQUEST = 400;

    int HTTP_OK = 200;

    /**
     * 5.biz status code definitions
     */
    int BIZ_NORMAL = 0;

    // 10_00_00 System_Module_ErrorCode
    int BIZ_SYSTEM_ERROR = 10_00_01;

    /**
     * 6.content type definitions
     */
    String RETURN_JSON_TYPE = "application/json";

    String RETURN_OCTET_TYPE = "application/octet-stream";
}
