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
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GmailService.class);
    private static final String CLIENT_SECRET_FILE = "/credentials.json";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String REDIRECT_URI = "http://localhost:8080/InThread/oauth2callback";

    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/gmail.modify",
            "https://www.googleapis.com/auth/gmail.send",
            "profile",
            "email"
    );

    private final Gmail service;

    public GmailService(Credential credential) {
        this.service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
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
                .setApprovalPrompt("force") // Always ask for consent to ensure refresh token
                .build();
    }

    public static Credential setUp(String authorizationCode) throws IOException {
        GoogleAuthorizationCodeFlow flow = getFlow();
        GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                .setRedirectUri(REDIRECT_URI)
                .execute();
        return flow.createAndStoreCredential(response, "user");
    }

    public void sendEmail(String to, String subject, String bodyHtml) throws MessagingException, IOException {
        LOGGER.info("Sending email to: {} with subject: {}", to, subject);

        MimeMessage email = createMultipartEmail(to, "me", subject, bodyHtml);
        Message message = createMessageWithEmail(email);

        Message sentMessage = service.users().messages().send("me", message).execute();

        LOGGER.info("Email sent to {}, Message ID: {}", to, sentMessage.getId());
    }

    private MimeMessage createMultipartEmail(String to, String from, String subject, String htmlContent) throws MessagingException {
        LOGGER.debug("Creating MIME email to: {}, from: {}, subject: {}", to, from, subject);

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Create multipart alternative: plain + HTML
        MimeBodyPart plainPart = new MimeBodyPart();
        plainPart.setText(stripHtmlTags(htmlContent), "utf-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(plainPart);
        multipart.addBodyPart(htmlPart);

        email.setContent(multipart);

        return email;
    }

    private String stripHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", "").replaceAll(" ", " ").trim();
    }

    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawBytes = buffer.toByteArray();
        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(rawBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }

    public List<Message> getInboxEmails() throws IOException {
        List<Message> fullMessages = new ArrayList<>();

        // List messages from INBOX
        ListMessagesResponse response = service.users().messages()
                .list("me")
                .setLabelIds(Arrays.asList("INBOX"))

                .setMaxResults(10L)
                .execute();

        List<Message> messages = response.getMessages();
        if (messages == null || messages.isEmpty()) {
            return fullMessages;
        }

        for (Message message : messages) {
            Message fullMessage = service.users().messages()
                    .get("me", message.getId())
                    .setFormat("full") // Needed to access headers and payload
                    .execute();

            fullMessages.add(fullMessage);
        }

        return fullMessages;
    }

    public Message getMessageDetails(String messageId) throws IOException {
        LOGGER.debug("Fetching message details for ID: {}", messageId);
        try {
            Message message = service.users().messages().get("me", messageId).setFormat("full").execute();
            LOGGER.info("Fetched details for message ID: {}", messageId);
            return message;
        } catch (IOException e) {
            LOGGER.error("Failed to fetch message details for ID: {}", messageId, e);
            throw e;
        }
    }
}