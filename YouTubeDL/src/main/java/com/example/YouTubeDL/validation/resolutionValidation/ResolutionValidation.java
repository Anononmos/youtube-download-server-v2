package com.example.YouTubeDL.validation.resolutionValidation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD, PARAMETER, LOCAL_VARIABLE })
@Retention(RUNTIME)
@Constraint(validatedBy = ResolutionValidator.class)
public @interface ResolutionValidation {
    
    String message() default "Invalid resolution";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
