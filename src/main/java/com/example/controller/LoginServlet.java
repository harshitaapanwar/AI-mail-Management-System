package com.example.controller;

import com.example.utils.GmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            GoogleAuthorizationCodeRequestUrl authUrl = new GoogleAuthorizationCodeRequestUrl(
                    GmailService.getFlow().getClientId(),
                    "http://localhost:8080/gmail-clone-oauth/oauth2callback",
                    GmailService.getFlow().getScopes()
            );
            authUrl.setAccessType("offline");
            resp.sendRedirect(authUrl.build());
            LOGGER.info("Redirected to Google OAuth2 authorization page");
        } catch (Exception e) {
            LOGGER.error("Failed to initiate OAuth2 flow", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to initiate login");
        }
    }
}
