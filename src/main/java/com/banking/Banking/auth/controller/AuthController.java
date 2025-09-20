package com.banking.Banking.auth.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.Banking.auth.dto.request.LoginRequest;
import com.banking.Banking.auth.dto.request.RefreshRequest;
import com.banking.Banking.auth.dto.response.AuthResponse;
import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.auth.service.concretes.RefreshTokenManager;
import com.banking.Banking.auth.service.concretes.TokenManager;
import com.banking.Banking.entity.User;
import com.banking.Banking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	  private final AuthenticationManager authenticationManager;
	    private final TokenManager tokenService;
	    private final RefreshTokenManager refreshTokenManager;
	    private final UserRepository userRepository;
	    
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
	        );
	        User user = (User) authentication.getPrincipal();

	        String accessToken = tokenService.generateAccessToken(user);
	        RefreshToken refreshToken = refreshTokenManager.create(user.getId());

	        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getId()));
	    }

	    @PostMapping("/refresh")
	    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
	        Optional<RefreshToken> refreshTokenOpt = refreshTokenManager.findById(request.getRefreshToken());

	        if (refreshTokenOpt.isEmpty() || !refreshTokenManager.validate(request.getRefreshToken())) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }

	        RefreshToken refreshToken = refreshTokenOpt.get();

	        
	        User user = userRepository.findById(refreshToken.getUserId())
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        String newAccessToken = tokenService.generateAccessToken(user);

	        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getId()));
	    }

	    @PostMapping("/logout")
	    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
	        refreshTokenManager.revoke(request.getRefreshToken());
	        return ResponseEntity.ok().build();
	    }
	}
