package com.example.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GmailService.class);
    private static final String CLIENT_SECRET_FILE = "/credentials.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String REDIRECT_URI = "http://localhost:8080/gmail-clone-oauth/oauth2callback";
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/gmail.readonly",
            "https://www.googleapis.com/auth/gmail.send"
    );

    private Gmail service;

    public GmailService(Credential credential) {
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Mail Management System")
                .build();
    }

    public static GoogleAuthorizationCodeFlow getFlow() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(GmailService.class.getResourceAsStream(CLIENT_SECRET_FILE))
        );
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
    }

    public Credential setUp(String authorizationCode) throws IOException {
        GoogleAuthorizationCodeFlow flow = getFlow();
        GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(REDIRECT_URI)
                .execute();
        return flow.createAndStoreCredential(response, "user");
    }

    public void sendEmail(String to, String subject, String bodyText) throws Exception {
        MimeMessage email = createEmail(to, "me", subject, bodyText);
        Message message = createMessageWithEmail(email);
        service.users().messages().send("me", message).execute();
        LOGGER.info("Email sent to {}", to);
    }

    public List<Message> getInboxEmails() throws IOException {
        return service.users().messages().list("me")
                .setMaxResults(10L)
                .setQ("from:me OR to:me")
                .execute()
                .getMessages();
    }

    public Message getMessageDetails(String messageId) throws IOException {
        return service.users().messages().get("me", messageId).setFormat("full").execute();
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private Message createMessageWithEmail(MimeMessage email) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}