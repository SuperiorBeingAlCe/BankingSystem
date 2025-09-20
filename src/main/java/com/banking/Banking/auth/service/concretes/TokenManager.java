package com.banking.Banking.auth.service.concretes;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenManager {

	 private final KeyPair keyPair; 
	    private final RefreshTokenManager refreshTokenManager;
	
	    // === ACCESS TOKEN ===
	    public String generateAccessToken(User user) {
	        Instant now = Instant.now();

	        return Jwts.builder()
	                .setSubject(String.valueOf(user.getId()))
	                .claim("roles", user.getRoles()) 
	                .setIssuedAt(Date.from(now))
	                .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES)))
	                .setId(UUID.randomUUID().toString()) 
	                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
	                .compact();
	    }

	    // === REFRESH TOKEN ===
	    public String generateRefreshToken(User user) {
	        
	        RefreshToken rt = refreshTokenManager.create(user.getId());
	       
	        return Jwts.builder()
	                .setSubject(String.valueOf(user.getId()))
	                .setIssuedAt(Date.from(Instant.now()))
	                .setExpiration(Date.from(rt.getExpiry()))
	                .setId(rt.getId())
	                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
	                .compact();
	    }

	    // === TOKEN DOĞRULAMA ===
	    public Jws<Claims> validateToken(String token) {
	        try {
	            return Jwts.parserBuilder()
	                    .setSigningKey(keyPair.getPublic())
	                    .build()
	                    .parseClaimsJws(token);
	        } catch (JwtException ex) {
	            throw new IllegalArgumentException("Invalid JWT", ex);
	        }
	    
}}
