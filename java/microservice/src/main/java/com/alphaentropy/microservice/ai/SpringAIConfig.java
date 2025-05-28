package com.alphaentropy.microservice.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

@Configuration
public class SpringAIConfig {

    @Value("${spring.ai.openai.api-key:#{null}}")
    private String apiKey;

    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(apiKey);
    }

    @Bean
    public OpenAiChatClient openAiChatClient(OpenAiApi openAiApi) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(model)
                .withTemperature(0.7f)
                .build();
        return new OpenAiChatClient(openAiApi, options);
    }
}
