package com.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class LogoutServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/");
            LOGGER.info("User logged out successfully");
        } catch (Exception e) {
            LOGGER.error("Logout failed", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Logout failed: " + e.getMessage());
        }
    }
}