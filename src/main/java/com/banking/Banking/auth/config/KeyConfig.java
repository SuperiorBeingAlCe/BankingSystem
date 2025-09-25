package com.banking.Banking.auth.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.banking.Banking.auth.util.PemUtils;

@Configuration
public class KeyConfig {
	
	@Bean
    public KeyPair keyPair() throws Exception {
        Path privatePath = new ClassPathResource("jwt.key.pkcs8").getFile().toPath();
        Path publicPath = new ClassPathResource("jwt.pub").getFile().toPath();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Private key
        byte[] privateBytes = PemUtils.readKeyBytes(privatePath);
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpecPrivate);

        // Public key
        byte[] publicBytes = PemUtils.readKeyBytes(publicPath);
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(publicBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpecPublic);

        return new KeyPair(publicKey, privateKey);
    }
	}