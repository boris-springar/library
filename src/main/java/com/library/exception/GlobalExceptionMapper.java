package com.library.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        Map<String, Object> error = new HashMap<>();
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String message = "An unexpected error occurred";

        // Handle Validation Errors
        if (exception instanceof ConstraintViolationException cve) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            message = "Validation failed";
            // You can iterate cve.getConstraintViolations() for detailed messages if needed
        }
        // Handle Application Errors (404, 409, etc.)
        else if (exception instanceof jakarta.ws.rs.WebApplicationException wae) {
            status = wae.getResponse().getStatus();
            message = wae.getMessage();
            // If the response already has an entity (like our custom JSON), return it
            if (wae.getResponse().hasEntity()) {
                return wae.getResponse();
            }
        }

        error.put("status", status);
        error.put("error", message);
        error.put("timestamp", java.time.Instant.now().toString());

        return Response.status(status)
                .entity(error)
                .build();
    }
}