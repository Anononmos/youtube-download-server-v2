package com.example.YouTubeDL.validation.resolutionValidation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ResolutionValidator implements ConstraintValidator<ResolutionValidation, Integer> {

    @Value("${application.resolutions}")
    private String resolutionsString;

    private Set<Integer> resolutions;

    @Override
    public void initialize(ResolutionValidation annotation) {
        // Initialization logic

        String[] resArray = resolutionsString.split(", ");
        resolutions = Arrays.asList(resArray)
                            .stream()
                            .map(Integer::parseInt)
                            .collect( Collectors.toSet() );
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
    public boolean isValid(Integer res, ConstraintValidatorContext context) {

        if ( !resolutions.contains(res) && res != null ) {
            String format = "%dp is an invalid resolution. Available resolutions [%s]";

            setErrorMessage(context, format, res, resolutionsString);

            return false;
        }

        return true;
    }
}