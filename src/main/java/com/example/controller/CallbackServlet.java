package com.example.controller;

import com.example.utils.GmailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
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
                Credential credential = GmailService.setUp(code); // Get token from code
                GmailService service = new GmailService(credential); // Now pass credential

                req.getSession().setAttribute("credential", credential);

                // Retrieve user profile using People API
                PeopleService peopleService = new PeopleService.Builder(
                        GmailService.HTTP_TRANSPORT,
                        GmailService.JSON_FACTORY,
                        credential
                ).setApplicationName("Mail Management System").build();

                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();

                String email = null;
                String name = null;

                // Extract email
                if (profile.getEmailAddresses() != null && !profile.getEmailAddresses().isEmpty()) {
                    email = profile.getEmailAddresses().get(0).getValue();
                }

                // Extract name
                if (profile.getNames() != null && !profile.getNames().isEmpty()) {
                    name = profile.getNames().get(0).getDisplayName();
                }

                // Fallback if name is not available
                if (name == null && email != null) {
                    name = email.split("@")[0];
                }

                if (email == null) {
                    throw new IOException("Could not retrieve user email");
                }

                req.getSession().setAttribute("userEmail", email);
                req.getSession().setAttribute("profileName", name);

                // Generate and store CSRF token
                String csrfToken = UUID.randomUUID().toString();
                req.getSession().setAttribute("csrfToken", csrfToken);

                resp.sendRedirect("send.jsp");
                LOGGER.info("OAuth2 callback successful, credential, profile, and CSRF token stored in session");
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