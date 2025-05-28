package com.alphaentropy.query.application.ai;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    
    @Bean
    public PromptTemplate queryPromptTemplate() {
        return new PromptTemplate("""
            You are a financial data analysis assistant. 
            Analyze the following query and provide insights:
            {query}
            
            Consider the following context:
            {context}
            """);
    }
} 