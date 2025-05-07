package com.example.controller;

import com.example.utils.GmailUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GmailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Credential credential = (Credential) session.getAttribute("credential");
        if (credential == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String to = request.getParameter("to");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        try {
            GmailUtil.sendEmail(credential, to, subject, message);
            response.sendRedirect("inbox.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Email sending failed.");
        }
    }


    public static List<Message> listMessages(Gmail service, String userId) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId)
                .setMaxResults(10L)
                .execute();
        List<Message> messages = new ArrayList<>();
        for (Message message : response.getMessages()) {
            Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
            messages.add(fullMessage);
        }
        return messages;
    }

}
