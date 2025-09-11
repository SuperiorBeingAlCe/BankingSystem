package com.banking.Banking;

import org.springframework.boot.SpringApplication;

public class TestBankingApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
