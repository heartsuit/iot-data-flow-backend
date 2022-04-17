package com.heartsuit.common.controller;

import com.heartsuit.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Heartsuit
 * @Date 2022-04-16
 */
@RestController
@RequestMapping("demo")
public class HelloController {
    @GetMapping("hello")
    public String hello() {
        return "hello everyone.";
    }

    @GetMapping("error")
    public Result error() {
        int value = 8 / 0;
        return Result.success(value);
    }
}
