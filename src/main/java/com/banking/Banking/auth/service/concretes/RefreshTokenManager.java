package com.banking.Banking.auth.service.concretes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenManager {

	 private final RefreshTokenRepository refreshTokenRepository;
	
	 
	 public RefreshToken create(Long userId) {
	        RefreshToken token = new RefreshToken();
	        token.setId(UUID.randomUUID().toString());
	        token.setUserId(userId);
	        token.setExpiry(Instant.now().plus(7, ChronoUnit.DAYS));

	        return refreshTokenRepository.save(token);
	    }

	    public boolean validate(String tokenId) {
	        return refreshTokenRepository.findById(tokenId)
	                .map(rt -> rt.getExpiry().isAfter(Instant.now()))
	                .orElse(false);
	    }
	    
	    public Optional<RefreshToken> findById(String tokenId) {
	        return refreshTokenRepository.findById(tokenId);
	    }

	    public void revoke(String tokenId) {
	        refreshTokenRepository.deleteById(tokenId);
	    }
}
