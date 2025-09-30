package com.banking.Banking.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import com.banking.Banking.exception.BusinessException;
import com.banking.Banking.exception.ErrorCode;
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
	        refreshTokenManager.validateOrThrow(request.getRefreshToken());
	        RefreshToken refreshToken = refreshTokenManager.findByIdOrThrow(request.getRefreshToken());
	        User user = userRepository.findById(refreshToken.getUserId())
	                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	        String newAccessToken = tokenService.generateAccessToken(user);
	        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getId()));
	    }

	        
	    

	    @PostMapping("/logout")
	    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
	        refreshTokenManager.revoke(request.getRefreshToken());
	        return ResponseEntity.ok().build();
	    }
	}
