package com.invoicing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyResponse {
    private Double score;
    private String explanation;
}

