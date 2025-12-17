package com.invoicing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Document number is required")
    private String docNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    private String address;
}

