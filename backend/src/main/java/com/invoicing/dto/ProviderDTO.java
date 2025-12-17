package com.invoicing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Tax ID is required")
    private String taxId;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String address;
}



