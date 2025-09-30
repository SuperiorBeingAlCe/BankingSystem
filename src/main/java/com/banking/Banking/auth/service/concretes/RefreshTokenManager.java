package com.banking.Banking.auth.service.concretes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.auth.repository.RefreshTokenRepository;
import com.banking.Banking.exception.BusinessException;
import com.banking.Banking.exception.ErrorCode;

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

	 public void validateOrThrow(String tokenId) {
		    RefreshToken token = findByIdOrThrow(tokenId);
		    if (token.getExpiry().isBefore(Instant.now())) {
		        throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		    }
		}
	    
	   
	 public void revoke(String tokenId) {
	        if (!refreshTokenRepository.existsById(tokenId)) {
	            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
	        }
	        refreshTokenRepository.deleteById(tokenId);
	    }
	    
	    public RefreshToken findByIdOrThrow(String tokenId) {
	        return refreshTokenRepository.findById(tokenId)
	                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
	    }
}
