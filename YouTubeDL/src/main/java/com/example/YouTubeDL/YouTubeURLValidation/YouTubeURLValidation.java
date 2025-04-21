package com.example.YouTubeDL.YouTubeURLValidation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = YouTubeURLValidator.class)
public @interface YouTubeURLValidation {

    String message() default "Invalid URL";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
