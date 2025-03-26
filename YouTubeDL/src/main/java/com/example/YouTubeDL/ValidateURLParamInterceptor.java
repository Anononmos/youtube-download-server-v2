package com.example.YouTubeDL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ValidateURLParamInterceptor implements HandlerInterceptor {

    public boolean validateURL(String url) throws URISyntaxException, IOException {
        // TODO: Implement cache for valid URLs

        int responseCode;
        HttpURLConnection conn;

        String validationUrlString = String.format("https://www.youtube.com/oembed?format=json&url=%s", url);

        URL validationUrl = new URI(validationUrlString).toURL();

        conn = (HttpURLConnection) validationUrl.openConnection();
        responseCode = conn.getResponseCode();

        conn.disconnect();

        // Internal server errors
        if (responseCode >= 500) {
            throw new IOException("YouTube responds with 500 status code.");
        }

        // Return false if 404 error code
        switch (responseCode) {

            case 400:
                return false;

            case 404:
                return false;
        
            default:
                return true;
        }
    }

    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String url = req.getParameter("url");

        System.out.println("Url: " + url);

        if (url == null) {
            res.sendError(HttpStatus.BAD_REQUEST.value() /* 400 error code */, "400 Query param \"url\" is not included in request.");
        }

        boolean isValid;

        try {
            isValid = validateURL(url);

        } catch (IOException err) {
            System.out.println("Failed to request YouTube's servers.");
            err.printStackTrace();

            res.sendError(500, "500 Connection error.");

            return false;
        }
        catch (URISyntaxException err) {
            System.out.println("Failed to parse requested URL");
            err.printStackTrace();

            res.sendError(400, "400 Value provided for param \"url\" does not follow URL schema.");

            return false;
        }

        if (!isValid) {
            res.sendError(404, "404 Value provided for param \"url\" does not link to existing video.");

            return false;
        }

        return true;
    }
}
