package com.banking.Banking.auth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.stream.Collectors;
public class PemUtils {

	 public static byte[] readKeyBytes(InputStream inputStream) throws IOException {
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
	            String pem = reader.lines()
	                    .filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END"))
	                    .collect(Collectors.joining());
	            return Base64.getDecoder().decode(pem);
	        }
	    }
}
