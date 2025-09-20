package com.banking.Banking.auth.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class TestKeyPairGenerator {

	public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }
	
}
