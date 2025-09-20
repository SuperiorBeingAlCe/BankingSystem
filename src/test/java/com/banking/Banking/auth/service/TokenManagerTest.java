package com.banking.Banking.auth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.banking.Banking.auth.service.concretes.RefreshTokenManager;
import com.banking.Banking.auth.service.concretes.TokenManager;
import com.banking.Banking.entity.Role;
import com.banking.Banking.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class TokenManagerTest {

	private TokenManager tokenManager;
    private KeyPair keyPair;
    private RefreshTokenManager refreshTokenManager;
    
    private User testUser;
	
    @BeforeEach
    void setup() throws Exception {
    	keyPair = TestKeyPairGenerator.generateKeyPair();
    	 refreshTokenManager = Mockito.mock(RefreshTokenManager.class);
    	 
    	 tokenManager = new TokenManager(keyPair, refreshTokenManager);
 
    	 Role userRole = Role.builder()
    		        .id(1L)
    		        .name("ROLE_USER")
    		        .build();
    	 
    	 testUser = User.builder()
    		        .id(1L)
    		        .username("testuser")
    		        .roles(Set.of(userRole))  
    		        .build();
       }
    
    @Test
    void generateAndValidateAccessToken() {
        String token = tokenManager.generateAccessToken(testUser);
        assertNotNull(token);

        Jws<Claims> claims = tokenManager.validateToken(token);
        assertEquals("1", claims.getBody().getSubject());
        assertEquals(Set.of("ROLE_USER"), claims.getBody().get("roles", Set.class));
    }

    @Test
    void validateTamperedTokenShouldFail() {
        String token = tokenManager.generateAccessToken(testUser);
        String tamperedToken = token + "abc";

        assertThrows(IllegalArgumentException.class, () -> tokenManager.validateToken(tamperedToken));
    }

    @Test
    void validateExpiredTokenShouldFail() {
      
        String token = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(java.util.Date.from(Instant.now().minusSeconds(3600)))
                .setExpiration(java.util.Date.from(Instant.now().minusSeconds(10)))
                .signWith(keyPair.getPrivate(), io.jsonwebtoken.SignatureAlgorithm.RS256)
                .compact();

        assertThrows(IllegalArgumentException.class, () -> tokenManager.validateToken(token));
    }
}
    	 
    
    

