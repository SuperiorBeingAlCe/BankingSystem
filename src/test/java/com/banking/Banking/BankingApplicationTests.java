package com.banking.Banking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.banking.Banking.auth.config.TestKeyConfig;

	
@SpringBootTest
@ActiveProfiles("test")
@Import(TestKeyConfig.class)
	class BankingApplicationTests {
	
		@Test
		void contextLoads() {
		}
	
	}
