package com.invoicing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private List<ProductDTO> products;
    private String reason;
}



