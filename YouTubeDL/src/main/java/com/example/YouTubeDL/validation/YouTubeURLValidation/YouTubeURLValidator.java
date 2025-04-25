package com.example.YouTubeDL.validation.YouTubeURLValidation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class YouTubeURLValidator implements ConstraintValidator<YouTubeURLValidation, String> {
    
    @Override
    public void initialize(YouTubeURLValidation annotation) {
        // Initialization logic
    }

    /**
     * 
     * @param format
     * @param context
     * @param args
     */
    private void setErrorMessage(ConstraintValidatorContext context, String format, Object... args) {
        String error = String.format(format, args);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(error).addConstraintViolation();

        System.out.println("Added error: " + error);
    }
    
    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        URL valdiationURL;

        try {
            String validationString = "https://www.youtube.com/oembed?format=json&url=" + url;

            valdiationURL = new URI(validationString).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            String format = "Request body parameter \"url\" [%s] is an invalid URL.";

            setErrorMessage(context, format, url);

            return false;
        }

        boolean isValid = true;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) valdiationURL.openConnection();
            int responseCode = conn.getResponseCode();

            HttpStatus status = HttpStatus.valueOf(responseCode);

            // Check if response status is 4XX or 5XX except non-embeddable videos (401 status code)
            if ( status.isError() && status != HttpStatus.UNAUTHORIZED ) {
                String format = "The video corresponding with the URL [%s] does not exist.";

                setErrorMessage(context, format, url);

                isValid = false;
            }
        }
        catch (IOException e) {
            String format = "Connection failure when validating \"url\" [%s].";

            setErrorMessage(context, format, url);

            isValid = false;
        }
        finally {
            
            if (conn != null) {
                conn.disconnect();
            }
        }

        return isValid;
    }   
}
