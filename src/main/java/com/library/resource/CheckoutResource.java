package com.library.resource;

import com.library.dto.CheckoutCreateDto;
import com.library.dto.CheckoutResponseDto;
import com.library.service.CheckoutService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/api/checkout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Checkout", description = "API endpoints for managing book checkouts")
public class CheckoutResource {

    @Inject
    CheckoutService checkoutService;

    @POST
    @Operation(summary = "Borrow a book")
    @APIResponse(responseCode = "201", description = "Book borrowed successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "Business rule violation")
    public Response checkoutBook(CheckoutCreateDto dto) {
        try {
            CheckoutResponseDto result = checkoutService.checkoutBook(dto);
            return Response.status(Response.Status.CREATED).entity(result).build();
        } catch (WebApplicationException e) {
            throw e;
        }
    }

    @POST
    @Path("/{id}/return")
    @Operation(summary = "Return a borrowed book")
    @APIResponse(responseCode = "200", description = "Book returned successfully")
    @APIResponse(responseCode = "404", description = "Checkout not found")
    @APIResponse(responseCode = "409", description = "Book already returned")
    public Response returnBook(@PathParam("id") UUID id) {
        CheckoutResponseDto result = checkoutService.returnBook(id);
        return Response.ok(result).build();
    }

    @GET
    @Operation(summary = "List active checkouts for a member")
    @APIResponse(responseCode = "200", description = "Checkouts retrieved successfully")
    public List<CheckoutResponseDto> getActiveCheckouts(@QueryParam("memberId") UUID memberId) {
        return checkoutService.getActiveCheckouts(memberId);
    }
}