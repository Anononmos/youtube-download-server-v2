package com.example.YouTubeDL;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class VideoController {

    private Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        return factory.getValidator();
    }
    
    @PostMapping(value="/offload", consumes="application/json")
    public @ResponseBody String offload(@Valid @RequestBody DownloadParams body) {

        Set<ConstraintViolation<DownloadParams>> violations = getValidator().validate(body);
        if ( !violations.isEmpty() ) {
            List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();

            for (String error : errors) {
                System.out.println(error);
            }
        }

        return null;
    }
    

    @GetMapping(value="/")
    public String index(@RequestParam String param) {
        return "Hello World";
    }
    
    
    @GetMapping(value="/download")
    public String getDownload( @RequestParam(value="url", required=true) String url ) {
        return new String("Hello World");
    }
    
}
