package com.example.YouTubeDL.validation.typeValidation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD, PARAMETER, LOCAL_VARIABLE})
@Retention(RUNTIME)
@Constraint(validatedBy = TypeValidator.class)
public @interface TypeValidation {
    
    String message() default "Invalid type";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
