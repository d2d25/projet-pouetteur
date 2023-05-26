package com.pouetteur.notificationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Component
public class KeyUtils {

    @Autowired
    Environment env;

    @Value("${access-token.public}")
    private String publicKeyPath;

    @Value("${refresh-token.public}")
    private String refreshPublicKeyPath;

    private RSAPublicKey publicKey;
    private RSAPublicKey refreshPublicKey;

    private RSAPublicKey getPublicKey(String publicKeyPath) {
        File publicKeyFile = new File(publicKeyPath);
        if (publicKeyFile.exists()) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("prod")))) {
                throw new RuntimeException("Public key not found");
            }
        }
        return null;
    }

    public RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = getPublicKey(publicKeyPath);
        }
        return publicKey;
    }

    public RSAPublicKey getRefreshPublicKey() {
        if (refreshPublicKey == null) {
            refreshPublicKey = getPublicKey(refreshPublicKeyPath);
        }
        return refreshPublicKey;
    }
}