package com.example.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;
import java.util.Base64;

public class GmailUtil {

    private static final String CLIENT_ID = "75653762447-vtcp1so96h931v439tpspescc494hd7g.apps.googleusercontent.com"; // 🔁 Replace with your actual Client ID
    private static final String CLIENT_SECRET = "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"; // 🔁 Replace with your actual Client Secret
    private static final String REDIRECT_URI = "http://localhost:8080/gmail-clone-oauth/oauth2callback"; // 🔁 Make sure it matches your Google Console config

    // Generate the Google OAuth2 URL to redirect the user for login and consent
    public static String getAuthorizationUrl() throws Exception {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                CLIENT_ID,
                CLIENT_SECRET,
                Collections.singletonList("https://www.googleapis.com/auth/gmail.modify")
        ).setAccessType("offline").build();

        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .set("prompt", "select_account") // 👈 Forces account selection each time
                .build();
    }

    // After user approves access and gives us the code, exchange it for tokens
    public static Credential exchangeCodeForTokens(String code) throws Exception {
        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                "https://oauth2.googleapis.com/token",
                CLIENT_ID,
                CLIENT_SECRET,
                code,
                REDIRECT_URI
        ).execute();

        return new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build()
                .setFromTokenResponse(response);
    }

    // Get Gmail service object using the OAuth2 credentials
    public static Gmail getGmailService(Credential credential) throws Exception {
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Gmail Clone").build();
    }

    // Use the authenticated Gmail API to send a message
    public static void sendEmail(Credential credential, String to, String subject, String bodyText) throws Exception {
        Gmail service = getGmailService(credential);

        MimeMessage mimeMessage = createEmail(to, "me", subject, bodyText);
        Message message = createMessageWithEmail(mimeMessage);
        service.users().messages().send("me", message).execute();
    }

    // Create the MimeMessage to be sent
    private static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    // Convert MimeMessage into a base64 encoded Gmail Message object
    private static Message createMessageWithEmail(MimeMessage emailContent) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail); // ✅ This works with the correct import
        return message;
    }

    // Optionally, you can refresh the token if it's expired (offline access case)
    public static Credential refreshCredentials(Credential credential) throws Exception {
        if (credential.getAccessToken() == null || credential.getExpiresInSeconds() <= 60) {
            // Refresh token if it's expired
            GoogleCredential refreshedCredential = new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build()
                    .setFromTokenResponse(new GoogleTokenResponse().setRefreshToken(credential.getRefreshToken()));

            refreshedCredential.refreshToken();
            return refreshedCredential;
        }
        return credential;
    }

}