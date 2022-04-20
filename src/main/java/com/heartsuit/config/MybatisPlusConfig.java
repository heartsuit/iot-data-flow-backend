package com.heartsuit.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Heartsuit
 * @Date 2020-03-24
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //TODO: mybatis-plus do not support TDengine, use postgresql Dialect
        paginationInterceptor.setDialectType("postgresql");
        return paginationInterceptor;
    }
}

