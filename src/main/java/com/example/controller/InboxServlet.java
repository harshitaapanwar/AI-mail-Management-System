package com.example.controller;

import com.example.utils.GmailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.model.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class InboxServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(InboxServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Credential credential = (Credential) req.getSession().getAttribute("credential");
            if (credential == null) {
                LOGGER.warn("No credentials found in session. Redirecting to login.");
                resp.sendRedirect("login"); // Adjust to your login URL
                return;
            }

            GmailService service = new GmailService(credential);
            List<Message> messages = service.getInboxEmails();

            if (messages != null) {
                req.setAttribute("messages", messages);
            }

            req.getRequestDispatcher("send.jsp").forward(req, resp); // ← Use inbox.jsp or rename send.jsp
        } catch (Exception e) {
            LOGGER.error("Failed to fetch inbox emails", e);
            req.setAttribute("error", "Failed to fetch emails: " + e.getMessage());
            req.getRequestDispatcher("inbox.jsp").forward(req, resp); // ← Same here
        }
    }
}
