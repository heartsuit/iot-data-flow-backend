package com.heartsuit.common.config;

import com.heartsuit.common.result.CodeMsg;
import com.heartsuit.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Heartsuit
 * @Date 2021-07-31
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> exception(Exception e) {
        log.error("Global exception: {}", null == e.getMessage() ? e.toString() : e.getMessage(), e);
        return Result.error(CodeMsg.SERVER_ERROR.getCode(), null == e.getMessage() ? e.toString() : e.getMessage());
    }
}
