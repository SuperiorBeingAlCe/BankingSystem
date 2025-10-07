package com.banking.Banking.auth.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.banking.Banking.auth.util.PemUtils;
@Profile("!test")
@Configuration
public class KeyConfig {
	@Bean
	public KeyPair keyPair() throws Exception {
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

	    try (
	        InputStream privateStream = getClass().getClassLoader().getResourceAsStream("jwt.key.pkcs8");
	        InputStream publicStream = getClass().getClassLoader().getResourceAsStream("jwt.pub")
	    ) {
	        if (privateStream == null || publicStream == null) {
	            throw new FileNotFoundException("JWT anahtar dosyaları bulunamadı.");
	        }

	        byte[] privateBytes = PemUtils.readKeyBytes(privateStream);
	        byte[] publicBytes = PemUtils.readKeyBytes(publicStream);

	        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
	        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));

	        return new KeyPair(publicKey, privateKey);
	    }
	}}
