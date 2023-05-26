package com.pouetteur.pouetservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Component
public class KeyUtils {

    @Autowired
    Environment env;

    @Value("${access-token.private}")
    private String secretKeyPath;

    @Value("${access-token.public}")
    private String publicKeyPath;

    @Value("${refresh-token.private}")
    private String refreshSecretKeyPath;

    @Value("${refresh-token.public}")
    private String refreshPublicKeyPath;

    private KeyPair keyPair;
    private KeyPair refreshKeyPair;

    private KeyPair getKeyPair() {
        if (keyPair == null) {
            keyPair = getKeyPair(secretKeyPath, publicKeyPath);
        }
        return keyPair;
    }

    private KeyPair getRefreshKeyPair() {
        if (refreshKeyPair == null) {
            refreshKeyPair = getKeyPair(refreshSecretKeyPath, refreshPublicKeyPath);
        }
        return refreshKeyPair;
    }

    private KeyPair getKeyPair(String secretKeyPath, String publicKeyPath) {
        KeyPair keyPair;

        File secretKeyFile = new File(secretKeyPath);
        File publicKeyFile = new File(publicKeyPath);

        if (secretKeyFile.exists() && publicKeyFile.exists()) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                byte[] privateKeyBytes = Files.readAllBytes(secretKeyFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                return new KeyPair(publicKey, privateKey);
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("prod")))) {
                throw new RuntimeException("Secret key or public key not found");
            }
        }


        File directory = new File("access-refresh-token-keys");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

            try (FileOutputStream fos = new FileOutputStream(publicKeyPath)) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            try (FileOutputStream fos = new FileOutputStream(secretKeyPath)) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            return keyPair;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) getKeyPair().getPublic();
    }

    public RSAPublicKey getRefreshPublicKey() {
        return (RSAPublicKey) getRefreshKeyPair().getPublic();
    }

    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) getKeyPair().getPrivate();
    }

    public RSAPrivateKey getRefreshPrivateKey() {
        return (RSAPrivateKey) getRefreshKeyPair().getPrivate();
    }
}
