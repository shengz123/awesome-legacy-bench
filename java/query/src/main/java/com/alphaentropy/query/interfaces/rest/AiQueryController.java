package com.alphaentropy.query.interfaces.rest;

import com.alphaentropy.query.application.ai.AiQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/query")
public class AiQueryController {
    
    private final AiQueryService aiQueryService;
    
    public AiQueryController(AiQueryService aiQueryService) {
        this.aiQueryService = aiQueryService;
    }
    
    @PostMapping
    public String processQuery(@RequestBody QueryRequest request) {
        return aiQueryService.processQuery(request.query(), request.context());
    }
    
    public record QueryRequest(String query, String context) {}
} 