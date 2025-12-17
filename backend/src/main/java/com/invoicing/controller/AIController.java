package com.invoicing.controller;

import com.invoicing.dto.AnomalyResponse;
import com.invoicing.dto.RecommendationResponse;
import com.invoicing.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    private final AIService aiService;
    
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam Long customerId) {
        return ResponseEntity.ok(aiService.getRecommendations(customerId));
    }
    
    @GetMapping("/anomaly-score")
    public ResponseEntity<AnomalyResponse> getAnomalyScore(
            @RequestParam Long invoiceId) {
        return ResponseEntity.ok(aiService.detectAnomaly(invoiceId));
    }
}

