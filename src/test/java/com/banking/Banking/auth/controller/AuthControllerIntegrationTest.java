package com.banking.Banking.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.banking.Banking.auth.dto.request.LoginRequest;
import com.banking.Banking.auth.dto.request.RefreshRequest;
import com.banking.Banking.auth.model.RefreshToken;
import com.banking.Banking.auth.service.concretes.RefreshTokenManager;
import com.banking.Banking.auth.service.concretes.TokenManager;
import com.banking.Banking.entity.User;
import com.banking.Banking.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenManager tokenService;

    @MockBean
    private RefreshTokenManager refreshTokenManager;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .roles(Set.of()) 
                .build();

        refreshToken = new RefreshToken();
        refreshToken.setId("refresh123");
        refreshToken.setUserId(1L);
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");

        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, Set.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(refreshTokenManager.create(1L)).thenReturn(refreshToken);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh123"));
    }

    @Test
    void refresh_shouldReturnNewAccessToken() throws Exception {
        RefreshRequest request = new RefreshRequest("refresh123");

        // validateOrThrow void, exception fırlatmazsa başarılı
        doNothing().when(refreshTokenManager).validateOrThrow("refresh123");
        when(refreshTokenManager.findByIdOrThrow("refresh123")).thenReturn(refreshToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.generateAccessToken(testUser)).thenReturn("new-access-token");

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh123"));
    }

    @Test
    void logout_shouldRevokeToken() throws Exception {
        RefreshRequest request = new RefreshRequest("refresh123");

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(refreshTokenManager).revoke("refresh123");
    }
}

