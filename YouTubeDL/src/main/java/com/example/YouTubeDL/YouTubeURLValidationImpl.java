package com.example.YouTubeDL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YouTubeURLValidationImpl implements ConstraintValidator<YouTubeURLValidation, String> {

    private void addErrorMessage(HttpStatus code, String message, ConstraintValidatorContext context) {
        String error = String.format("%d %s", code.value(), message);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(error).addConstraintViolation();

        System.out.println(message);
    }

    @Override
    public void initialize(YouTubeURLValidation annotation) {
        // Initialization logic
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {

        URL validationUrl;

        // Create URL
        try {
            validationUrl = new URI("https://www.youtube.com/oembed?format=json&url=" + url).toURL();
        }
        catch (URISyntaxException | MalformedURLException e) {
            String error = String.format("Query param \"url\" [%s] is not a proper url.", url);

            addErrorMessage(HttpStatus.BAD_REQUEST, error, context);

            return false;
        }

        HttpURLConnection conn = null;
        boolean output = true;

        try {
            conn = (HttpURLConnection) validationUrl.openConnection();
            int responseCode = conn.getResponseCode();

            System.out.println( String.format("Embed URL [%s] returns status code: %d", url, responseCode) );

            HttpStatus status = HttpStatus.valueOf(responseCode);

            // Check for 4XX or 5XX error except for 401 error (caused by video being non-embeddable)
            if ( status.isError() && status != HttpStatus.UNAUTHORIZED) {
                String error = String.format("Video corresponding with URL [%s] does not exist", url);

                addErrorMessage(HttpStatus.NOT_FOUND, error, context);

                output = false;
            }
        }
        // Connection failed
        catch (IOException e) {
            String error = "Failed to connect to YouTube.";

            addErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, error, context);
            
            output = false;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return output;
    }
}
