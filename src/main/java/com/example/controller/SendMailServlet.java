package com.example.controller;

import com.example.utils.GmailService;
import com.google.api.client.auth.oauth2.Credential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

public class SendMailServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMailServlet.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Credential credential = (Credential) req.getSession().getAttribute("credential");
        String sessionCsrfToken = (String) req.getSession().getAttribute("csrfToken");
        String requestCsrfToken = req.getParameter("csrfToken");

        if (credential == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service not initialized. Please authorize the application.");
            return;
        }

        if (sessionCsrfToken == null || !sessionCsrfToken.equals(requestCsrfToken)) {
            LOGGER.warn("CSRF token mismatch or missing");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String to = req.getParameter("to");
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");

        if (to == null || subject == null || body == null || to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields (to, subject, body) must be filled.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(to).matches()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email address.");
            return;
        }

        try {
            GmailService service = new GmailService(credential);
            service.sendEmail(to, subject, body);
            resp.getWriter().println("Email sent successfully.");
            LOGGER.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            LOGGER.error("Failed to send email", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send email: " + e.getMessage());
        }
    }
}
