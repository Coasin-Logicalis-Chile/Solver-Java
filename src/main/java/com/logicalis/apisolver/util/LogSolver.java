package com.logicalis.apisolver.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class LogSolver
{
    public static void insertInitService(String owner, String url, String httpMethod){
        log.info(owner + " SERVICE_INIT " + " URL " + url + " METHOD " + httpMethod);
    }

    public static void insertResponse(String owner, String url, String httpMethod, String payload, String httpResponse, HttpStatus httpResponseStatus, String headers){
        log.info(owner + " SERVICE_RESPONSE " + url);
        log.info(owner + " SERVICE_METHOD " + httpMethod);
        log.debug(owner + " SERVICE_HEADERS " + headers);
        log.debug(owner + " SERVICE_PAYLOAD " + payload);
        log.debug(owner + " SERVICE_RESPONSE " + httpResponse);
        log.info(owner + " SERVICE_STATUS " + httpResponseStatus);
    }

    public static void insertEndService(String owner, String url, String httpMethod){
        log.info(owner + " SERVICE_END" + " URL " + url + " METHOD " + httpMethod);
    }
}
