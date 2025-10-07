package com.banking.Banking.auth.config;

import java.security.KeyPair;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.banking.Banking.auth.service.TestKeyPairGenerator;
@TestConfiguration
@Profile("test")
public class TestKeyConfig {
    @Bean
    public KeyPair keyPair() throws Exception {
        return new TestKeyPairGenerator().generateKeyPair();
    }
}