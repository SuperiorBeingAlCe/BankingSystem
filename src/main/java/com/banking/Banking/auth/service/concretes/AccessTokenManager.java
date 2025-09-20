package com.banking.Banking.auth.service.concretes;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenManager {
	
	private final KeyPair keyPair;
	
	 public String create(Long userId, List<String> roles) {
	        Instant now = Instant.now();
	        return Jwts.builder()
	                .setSubject(String.valueOf(userId))
	                .claim("roles", roles)
	                .setIssuedAt(Date.from(now))
	                .setExpiration(Date.from(now.plus(10, ChronoUnit.MINUTES)))
	                .setId(UUID.randomUUID().toString()) 
	                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
	                .compact();
	    }
	
	

}
