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
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class SendMailServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMailServlet.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            // Retrieve user credentials and CSRF token
            Credential credential = (Credential) req.getSession().getAttribute("credential");
            String sessionCsrfToken = (String) req.getSession().getAttribute("csrfToken");
            String requestCsrfToken = req.getParameter("csrfToken");

            if (credential == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\":\"You must authenticate before sending emails.\"}");
                LOGGER.warn("Missing credentials in session");
                return;
            }

            if (sessionCsrfToken == null || requestCsrfToken == null || !sessionCsrfToken.equals(requestCsrfToken)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"error\":\"Invalid CSRF token. Please refresh the page and try again.\"}");
                LOGGER.warn("CSRF validation failed: session={}, request={}", sessionCsrfToken, requestCsrfToken);
                return;
            }

            // Extract parameters from request
            String to = req.getParameter("to");
            String subject = req.getParameter("subject");
            String body = req.getParameter("body"); // body is expected to be HTML

            if (to == null || subject == null || body == null || to.trim().isEmpty() || subject.trim().isEmpty() || body.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\":\"Please fill all required fields.\"}");
                LOGGER.warn("Missing fields: to={}, subject={}, body={}", to, subject, body);
                return;
            }

            if (!EMAIL_PATTERN.matcher(to).matches()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\":\"Invalid email format.\"}");
                LOGGER.warn("Invalid email address: {}", to);
                return;
            }

            // Initialize GmailService with the user's credential
            GmailService gmailService = new GmailService(credential);

            // Send email (HTML body supported)
            gmailService.sendEmail(to, subject, body);

            resp.setStatus(HttpServletResponse.SC_OK);
            out.println("{\"success\":\"Email sent successfully.\"}");
            LOGGER.info("Email successfully sent to {}", to);

        } catch (Exception e) {
            LOGGER.error("Error sending email", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\":\"Failed to send email: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        } finally {
            out.close();
        }
    }
}
