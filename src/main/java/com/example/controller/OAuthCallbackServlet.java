package com.example.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.example.utils.GmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;


public class OAuthCallbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code != null) {
            Credential credential;
            try {
                credential = GmailUtil.exchangeCodeForTokens(code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            HttpSession session = request.getSession();
            session.setAttribute("credential", credential);
            response.sendRedirect("inbox.jsp");
        } else {
            response.getWriter().println("Authorization failed.");
        }
    }
}

