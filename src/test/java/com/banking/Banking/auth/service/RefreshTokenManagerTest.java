package com.banking.Banking.auth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.auth.repository.RefreshTokenRepository;
import com.banking.Banking.auth.service.concretes.RefreshTokenManager;
import com.banking.Banking.exception.BusinessException;

public class RefreshTokenManagerTest {
	  private RefreshTokenRepository refreshTokenRepository;
	    private RefreshTokenManager refreshTokenManager;

	    @BeforeEach
	    void setUp() {
	        refreshTokenRepository = mock(RefreshTokenRepository.class);
	        refreshTokenManager = new RefreshTokenManager(refreshTokenRepository);
	    }

	    @Test
	    void createToken_shouldSaveAndReturn() {
	        Long userId = 1L;
	        RefreshToken token = new RefreshToken();
	        token.setId(UUID.randomUUID().toString());
	        token.setUserId(userId);
	        token.setExpiry(Instant.now().plus(7, ChronoUnit.DAYS));

	        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(token);

	        RefreshToken created = refreshTokenManager.create(userId);
	        assertNotNull(created);
	        assertEquals(userId, created.getUserId());

	        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
	        verify(refreshTokenRepository).save(captor.capture());
	        assertEquals(userId, captor.getValue().getUserId());
	    }

	    @Test
	    void validateOrThrow_shouldNotThrowIfValid() {
	        String tokenId = "token1";
	        RefreshToken token = new RefreshToken();
	        token.setId(tokenId);
	        token.setExpiry(Instant.now().plus(1, ChronoUnit.DAYS));

	        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

	        assertDoesNotThrow(() -> refreshTokenManager.validateOrThrow(tokenId));
	    }

	    @Test
	    void validateOrThrow_shouldThrowIfExpired() {
	        String tokenId = "token2";
	        RefreshToken token = new RefreshToken();
	        token.setId(tokenId);
	        token.setExpiry(Instant.now().minus(1, ChronoUnit.DAYS));

	        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

	        assertThrows(BusinessException.class, () -> refreshTokenManager.validateOrThrow(tokenId));
	    }

	    @Test
	    void revokeToken_shouldCallDeleteById() {
	        String tokenId = "token3";

	        
	        RefreshToken token = new RefreshToken();
	        token.setId(tokenId);

	        
	        when(refreshTokenRepository.existsById(tokenId)).thenReturn(true);
	        doNothing().when(refreshTokenRepository).deleteById(tokenId);

	         
	        refreshTokenManager.revoke(tokenId);

	        
	        verify(refreshTokenRepository).deleteById(tokenId);
	    }
	    @Test
	    void findByIdOrThrow_shouldReturnToken() {
	        String tokenId = "token4";
	        RefreshToken token = new RefreshToken();
	        token.setId(tokenId);

	        when(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

	        RefreshToken result = refreshTokenManager.findByIdOrThrow(tokenId);
	        assertEquals(tokenId, result.getId());
	    }
}
