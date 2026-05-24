package com.library.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();

        Set<jakarta.validation.ConstraintViolation<?>> violations = exception.getConstraintViolations();

        for (jakarta.validation.ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errors)
                .build();
    }
}