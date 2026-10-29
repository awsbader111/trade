package com.example.trade;

import com.oanda.v20.account.AccountID;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    private Config() {
    }

    private static final Properties CREDENTIALS = loadCredentials();

    public static final String URL = "https://api-fxpractice.oanda.com";
    public static final AccountID ACCOUNTID =
            new AccountID(requiredCredential("ACCOUNT_ID"));
    public static final String TOKEN = requiredCredential("TOKEN");

    private static Properties loadCredentials() {
        Properties properties = new Properties();
        Path credentialsFile = Path.of("password.txt");

        if (!Files.exists(credentialsFile)) {
            throw new IllegalStateException(
                    "password.txt was not found in the project directory");
        }

        try (InputStream input = Files.newInputStream(credentialsFile)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read password.txt", e);
        }
    }

    private static String requiredCredential(String name) {
        String value = CREDENTIALS.getProperty(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Set " + name + " in password.txt");
        }

        return value.trim();
    }
}