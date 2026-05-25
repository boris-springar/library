package com.library.resource;

import com.library.dto.BookCreateDto;
import com.library.dto.BookResponseDto;
import com.library.service.BookService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book management endpoints")
public class BookResource {

    @Inject
    BookService bookService;

    @POST
    @Operation(summary = "Add a new book")
    @APIResponse(responseCode = "201", description = "Book created successfully")
    @APIResponse(responseCode = "409", description = "ISBN already exists")
    public Response createBook(@Valid BookCreateDto dto) {
        BookResponseDto result = bookService.createBook(dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Operation(summary = "List all books with pagination")
    @APIResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<BookResponseDto> listBooks(
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size) {
        return bookService.listBooks(page, size);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a single book by ID")
    @APIResponse(responseCode = "200", description = "Book found")
    @APIResponse(responseCode = "404", description = "Book not found")
    public Response getBook(@PathParam("id") UUID id) {
        return Response.ok(bookService.getBook(id)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing book")
    @APIResponse(responseCode = "200", description = "Book updated successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "ISBN conflict")
    public Response updateBook(@PathParam("id") UUID id, @Valid BookResponseDto dto) {
        return Response.ok(bookService.updateBook(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a book")
    @APIResponse(responseCode = "204", description = "Book deleted successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "Cannot delete - copies are borrowed")
    public Response deleteBook(@PathParam("id") UUID id) {
        bookService.deleteBook(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search books by author and/or title")
    @APIResponse(responseCode = "200", description = "Search results")
    public List<BookResponseDto> searchBooks(
            @QueryParam("author") String author,
            @QueryParam("title") String title) {
        return bookService.searchBooks(author, title);
    }
}