package com.alphaentropy.microservice.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.prompt.UserPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class LLMService {

    @Autowired
    private ChatClient chatClient;

    /**
     * Generate a response from the LLM based on a user query
     * @param query The user's query
     * @param context Additional context for the query
     * @return The LLM's response
     */
    public String generateResponse(String query, Map<String, Object> context) {
        try {
            String systemPrompt = "You are a financial data assistant. Provide concise and accurate information about stocks and financial data. " +
                    "Use the following context to inform your response: {context}";
            
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
            String systemMessage = systemPromptTemplate.render(context);
            
            String userPromptText = "Query: {query}";
            UserPromptTemplate userPromptTemplate = new UserPromptTemplate(userPromptText);
            String userMessage = userPromptTemplate.render(Map.of("query", query));
            
            Prompt prompt = new Prompt(systemMessage, userMessage);
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("Error generating LLM response", e);
            return "Sorry, I couldn't process your request at this time.";
        }
    }
    
    /**
     * Analyze financial data and provide insights
     * @param symbol Stock symbol
     * @param data Financial data in JSON format
     * @return Analysis and insights
     */
    public String analyzeFinancialData(String symbol, String data) {
        try {
            String systemPrompt = "You are a financial analyst. Analyze the following financial data for {symbol} " +
                    "and provide key insights and recommendations.";
            
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
            String systemMessage = systemPromptTemplate.render(Map.of("symbol", symbol));
            
            String userPromptText = "Financial Data: {data}";
            UserPromptTemplate userPromptTemplate = new UserPromptTemplate(userPromptText);
            String userMessage = userPromptTemplate.render(Map.of("data", data));
            
            Prompt prompt = new Prompt(systemMessage, userMessage);
            ChatResponse response = chatClient.call(prompt);
            
            return response.getResult().getOutput().getContent();
        } catch (Exception e) {
            log.error("Error analyzing financial data", e);
            return "Sorry, I couldn't analyze the financial data at this time.";
        }
    }
}
