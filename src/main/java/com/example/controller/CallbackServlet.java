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
import java.util.UUID;

public class CallbackServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code != null) {
            try {
                GmailService service = new GmailService(null); // Temporary for auth
                Credential credential = service.setUp(code);
                req.getSession().setAttribute("credential", credential);

                // Generate and store CSRF token in session
                String csrfToken = UUID.randomUUID().toString();
                req.getSession().setAttribute("csrfToken", csrfToken);

                resp.sendRedirect("send.jsp");
                LOGGER.info("OAuth2 callback successful, credential and CSRF token stored in session");
            } catch (Exception e) {
                LOGGER.error("OAuth2 setup failed", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Setup failed: " + e.getMessage());
            }
        } else {
            LOGGER.warn("OAuth2 authorization failed: no code parameter");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization failed");
        }
    }
}
