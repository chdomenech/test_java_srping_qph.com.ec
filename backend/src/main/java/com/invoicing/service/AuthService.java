package com.invoicing.service;

import com.invoicing.dto.LoginRequest;
import com.invoicing.dto.LoginResponse;
import com.invoicing.entity.Role;
import com.invoicing.entity.User;
import com.invoicing.repository.UserRepository;
import com.invoicing.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    public AuthService(AuthenticationManager authenticationManager,
                      JwtTokenProvider tokenProvider,
                      UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        
        return new LoginResponse(token, user.getUsername(), roles);
    }
}



