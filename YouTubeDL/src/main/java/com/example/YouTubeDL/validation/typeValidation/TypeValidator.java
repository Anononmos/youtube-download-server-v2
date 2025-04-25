package com.example.YouTubeDL.validation.typeValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeValidator implements ConstraintValidator<TypeValidation, String> {
    
    @Override
    public void initialize(TypeValidation annotation) {
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
    public boolean isValid(String type, ConstraintValidatorContext context) {
        String format = "Type must be 'audio' or the default value 'video'.";

        System.err.println("Type: " + type);

        if (type == null) {
            return true;
        }

        switch(type) {
            case "video": case "audio":
                return true;

            default:
                setErrorMessage(context, format);

                return false;
        }
    }
}
