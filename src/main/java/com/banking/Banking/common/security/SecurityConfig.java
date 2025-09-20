package com.banking.Banking.common.security;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	 private final JwtFilter jwtFilter;
	 
	 @Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	     http
	         .csrf(csrf -> csrf.disable()) 
	         .authorizeHttpRequests(auth -> auth
	             .requestMatchers("/auth/**").permitAll()
	             .anyRequest().hasAnyRole("USER", "ADMIN")
	         )
	         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	     return http.build();
	 }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
	    @Bean
	    public JwtDecoder jwtDecoder() throws Exception {
	        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
	            .generatePublic(new X509EncodedKeySpec(Files.readAllBytes(Paths.get("jwt.pub"))));
	        return NimbusJwtDecoder.withPublicKey(publicKey).build();
	    }
	}