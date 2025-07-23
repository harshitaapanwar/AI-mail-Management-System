package com.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class TranslateServlet extends HttpServlet {

    private static final String API_URL = "https://libretranslate.com/translate";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\": \"GET method not allowed. Use POST instead.\"}");
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String text = req.getParameter("text");
        String targetLang = req.getParameter("targetLang");

        if (text == null || targetLang == null || text.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Missing text or target language\"}");
            return;
        }

        try {
            String urlParams = "q=" + URLEncoder.encode(text, "UTF-8")
                    + "&source=auto"
                    + "&target=" + URLEncoder.encode(targetLang.trim(), "UTF-8")
                    + "&format=text";  // Use 'text' format here

            URL url = new URL("https://libretranslate.com/translate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(urlParams.getBytes("UTF-8"));
                os.flush();
            }

            int status = conn.getResponseCode();

            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }
            reader.close();

            if (status >= 200 && status < 300) {
                // Success - output JSON response from LibreTranslate
                out.write(responseStr.toString());
            } else {
                // Error - return JSON with error message
                resp.setStatus(status);
                out.write("{\"error\": \"Translation failed with status " + status + ": " + responseStr.toString().replace("\"", "\\\"") + "\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Translation failed: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

}
