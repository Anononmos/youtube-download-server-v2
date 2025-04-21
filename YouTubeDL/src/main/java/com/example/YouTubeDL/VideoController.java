package com.example.YouTubeDL;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.YouTubeDL.YouTubeURLValidation.DownloadValidationErrorType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class VideoController {

    private Validator geValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        return factory.getValidator();
    }
    
    @PostMapping(value = "/offload", consumes = "application/json", produces = "application/json")
    public @ResponseBody String offload(@RequestBody DownloadRequest body) {

        Set<ConstraintViolation<DownloadRequest>> violations = geValidator().validate(body);

        if ( !violations.isEmpty() ) {
            List<ConstraintViolation<DownloadRequest>> vList = violations.stream().toList();

            // TODO: collate violations into unique lists and throw custom exceptions for each

            for (var violation : vList) {
                var payload = violation.getConstraintDescriptor().getPayload();

                if ( payload.contains(DownloadValidationErrorType.MissingURL.class) ) {
                    System.out.println("Missing URL: " + violation.getMessage());

                    
                }

                if ( payload.contains(DownloadValidationErrorType.InvalidURL.class) ) {
                    System.out.println("Invalid URL: " + violation.getMessage());
                }

                // TODO: Implement resolution check
            }
        }

        return "Hello";
    }
    

    @GetMapping(value = "/")
    public String index(@RequestParam String param) {
        return "Hello World";
    }
    
    
    @GetMapping(value = "/download")
    public String getDownload( @RequestParam(value = "url", required = true) String url ) {
        return new String("Hello World");
    }
    
}
