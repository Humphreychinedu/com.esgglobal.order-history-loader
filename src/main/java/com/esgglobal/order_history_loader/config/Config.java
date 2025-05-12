package com.esgglobal.order_history_loader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
