package com.example.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

import java.io.IOException;

public class GmailUtil {
    public static String getAuthorizationUrl() throws IOException {
        GoogleAuthorizationCodeFlow flow = GmailService.getFlow();
        return new GoogleAuthorizationCodeRequestUrl(
                flow.getClientId(),
                "http://localhost:8080/InThread/oauth2callback", // Corrected URI
                flow.getScopes()
        ).setAccessType("offline").build();
    }
}