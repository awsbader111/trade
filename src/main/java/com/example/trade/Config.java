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
    public static final String ACCOUNT_ID = requiredCredential("ACCOUNT_ID");
    public static final AccountID ACCOUNTID =
            new AccountID(ACCOUNT_ID);
    public static final String TOKEN = requiredCredential("TOKEN");

    private static Properties loadCredentials() {
        Properties properties = new Properties();
        Path credentialsFile = Path.of("password.txt");

        if (!Files.exists(credentialsFile)) {
            return properties;
        }

        try (InputStream input = Files.newInputStream(credentialsFile)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read password.txt", e);
        }
    }

    private static String requiredCredential(String name) {
        String environmentName = name.equals("ACCOUNT_ID") ? "OANDA_ACCOUNT_ID" : "OANDA_TOKEN";
        String fromEnvironment = System.getenv(environmentName);
        String value = (fromEnvironment != null && !fromEnvironment.isBlank())
                ? fromEnvironment : CREDENTIALS.getProperty(name, "");
        return value.trim();
    }
}