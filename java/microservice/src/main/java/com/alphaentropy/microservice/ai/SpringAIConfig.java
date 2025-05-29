package com.alphaentropy.microservice.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Temporary implementation of SpringAIConfig without Spring AI dependency
 * to allow the build to succeed. This will be replaced with the proper
 * Spring AI implementation when the dependency is available.
 */
@Configuration
public class SpringAIConfig {

    @Value("${spring.ai.openai.api-key:#{null}}")
    private String apiKey;

    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Bean
    public String openAiApiKey() {
        return apiKey;
    }

    @Bean
    public String openAiModel() {
        return model;
    }
}
