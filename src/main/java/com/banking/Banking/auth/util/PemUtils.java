package com.banking.Banking.auth.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class PemUtils {
	
	public static byte[] readKeyBytes(Path path) throws IOException {
        String pem = Files.readString(path);
        pem = pem
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }
	
}
