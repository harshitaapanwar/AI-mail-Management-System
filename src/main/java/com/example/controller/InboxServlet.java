package com.example.controller;

import com.example.utils.GmailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.model.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class InboxServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(InboxServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Credential credential = (Credential) req.getSession().getAttribute("credential");
        if (credential != null) {
            try {
                GmailService service = new GmailService(credential);
                List<Message> messageList = service.getInboxEmails();
                List<Message> detailedMessages = new ArrayList<>();
                for (Message msg : messageList) {
                    detailedMessages.add(service.getMessageDetails(msg.getId()));
                }
                req.setAttribute("messages", detailedMessages);
                req.getRequestDispatcher("inbox.jsp").forward(req, resp);
                LOGGER.info("Retrieved {} inbox messages", detailedMessages.size());
            } catch (Exception e) {
                LOGGER.error("Failed to retrieve emails", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve emails: " + e.getMessage());
            }
        } else {
            LOGGER.warn("Gmail service not initialized");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Gmail service not initialized.");
        }
    }
}