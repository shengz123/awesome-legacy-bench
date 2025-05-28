package com.alphaentropy.query.application.ai;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AiQueryService {
    
    private final ChatClient chatClient;
    private final PromptTemplate queryPromptTemplate;
    
    public AiQueryService(ChatClient chatClient, PromptTemplate queryPromptTemplate) {
        this.chatClient = chatClient;
        this.queryPromptTemplate = queryPromptTemplate;
    }
    
    public String processQuery(String query, String context) {
        String prompt = queryPromptTemplate.create(Map.of(
            "query", query,
            "context", context
        ));
        
        return chatClient.generate(prompt).getGeneration().getText();
    }
} 