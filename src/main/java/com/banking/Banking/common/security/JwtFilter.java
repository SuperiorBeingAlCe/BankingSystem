package com.banking.Banking.common.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.banking.Banking.auth.service.concretes.TokenManager;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	 private final TokenManager tokenService;
	
	   @Override
	    protected void doFilterInternal(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain filterChain)
	            throws ServletException, IOException {

	        String authHeader = request.getHeader("Authorization");
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            String token = authHeader.substring(7);
	            try {
	                Claims claims = tokenService.validateToken(token).getBody();
	                Long userId = Long.valueOf(claims.getSubject());
	                List<GrantedAuthority> authorities = ((List<String>) claims.get("roles"))
	                        .stream().map(SimpleGrantedAuthority::new)
	                        .collect(Collectors.toList());
	                UsernamePasswordAuthenticationToken auth =
	                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
	                SecurityContextHolder.getContext().setAuthentication(auth);
	            } catch (Exception ex) {
	                SecurityContextHolder.clearContext();
	            }
	        }
	        filterChain.doFilter(request, response);
	    }
	 
}
