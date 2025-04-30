package com.example.controller;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;


public class FetchEmailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Gmail service = (Gmail) session.getAttribute("gmailService");

        if (service == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get the category (primary, social, etc.) from the request parameter
        String category = request.getParameter("category");
        String query = ""; // default query if no category is provided

        // Add filters for categories if you need them (this is optional and depends on how you want to categorize emails)
        if (category != null && !category.isEmpty()) {
            // Example: apply different queries based on categories
            switch (category) {
                case "primary":
                    query = "category:primary";
                    break;
                case "social":
                    query = "category:social";
                    break;
                case "important":
                    query = "is:important";
                    break;
                case "promotions":
                    query = "category:promotions";
                    break;
                // Add more categories or filters as needed
            }
        }

        try {
            // Fetch messages with the applied query
            ListMessagesResponse listResponse = service.users().messages().list("me")
                    .setMaxResults(10L)
                    .setQ(query)  // apply the filter query
                    .execute();

            List<Map<String, String>> processedMessages = new ArrayList<>();
            if (listResponse.getMessages() != null) {
                for (Message m : listResponse.getMessages()) {
                    Message fullMessage = service.users().messages().get("me", m.getId()).execute();

                    // Extract headers like "From", "Subject", and "Date"
                    Map<String, String> emailDetails = new HashMap<>();
                    for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                        if ("From".equals(header.getName())) {
                            emailDetails.put("From", header.getValue());
                        } else if ("Subject".equals(header.getName())) {
                            emailDetails.put("Subject", header.getValue());
                        } else if ("Date".equals(header.getName())) {
                            emailDetails.put("Date", header.getValue());
                        }
                    }

                    processedMessages.add(emailDetails);
                }
            }

            // Return the messages as JSON response for the frontend to handle
            response.setContentType("application/json");
            response.getWriter().write(new com.google.gson.Gson().toJson(processedMessages));

        } catch (Exception e) {
            e.printStackTrace();
            // In case of error, return an error message as JSON
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Failed to fetch emails\"}");
        }

    }
}
