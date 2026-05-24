package com.library.resource;

import com.library.dto.BookDto;
import com.library.dto.PageResponse;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.util.Mapper;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book management endpoints")
public class BookResource {

    @Inject
    BookRepository bookRepository;

    /**
     * POST /api/books - Add a new book
     */
    @POST
    @Transactional
    @Operation(summary = "Add a new book")
    @APIResponse(responseCode = "201", description = "Book created successfully")
    @APIResponse(responseCode = "400", description = "Validation error or duplicate ISBN")
    public Response createBook(@Valid BookDto dto) {
        if (bookRepository.existsByIsbn(dto.isbn())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"ISBN already exists\"}")
                    .build();
        }

        Book book = Mapper.toEntity(dto);
        bookRepository.persist(book);

        return Response.status(Response.Status.CREATED)
                .entity(Mapper.toDto(book))
                .build();
    }

    /**
     * GET /api/books - List all books with pagination
     */
    @GET
    @Operation(summary = "List all books with pagination")
    @APIResponse(responseCode = "200", description = "Books retrieved successfully")
    public PageResponse<BookDto> listBooks(
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size) {

        if (page < 0 || size <= 0 || size > 100) {
            throw new BadRequestException("Invalid pagination parameters");
        }

        long total = bookRepository.count();

        // Use Panache's Page instead of jakarta.data.Page
        List<Book> books = bookRepository.findAll()
                .page(Page.of(page, size))
                .list();

        List<BookDto> dtos = books.stream()
                .map(Mapper::toDto)
                .toList();

        return PageResponse.of(dtos, page, size, total);
    }

    /**
     * GET /api/books/{id} - Get a single book
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get a single book by ID")
    @APIResponse(responseCode = "200", description = "Book found")
    @APIResponse(responseCode = "404", description = "Book not found")
    public Response getBook(@PathParam("id") Long id) {
        Book book = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        return Response.ok(Mapper.toDto(book)).build();
    }

    /**
     * PUT /api/books/{id} - Update an existing book
     */
    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update an existing book")
    @APIResponse(responseCode = "200", description = "Book updated successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "ISBN conflict")
    public Response updateBook(@PathParam("id") Long id, @Valid BookDto dto) {
        Book existing = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        if (!existing.getIsbn().equals(dto.isbn()) && bookRepository.existsByIsbn(dto.isbn())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"ISBN already exists\"}")
                    .build();
        }

        existing.setTitle(dto.title());
        existing.setAuthor(dto.author());
        existing.setIsbn(dto.isbn());
        existing.setPublicationYear(dto.publicationYear());
        existing.setTotalCopies(dto.totalCopies());

        bookRepository.persist(existing);

        return Response.ok(Mapper.toDto(existing)).build();
    }

    /**
     * DELETE /api/books/{id} - Remove a book (only if no copies are borrowed)
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a book")
    @APIResponse(responseCode = "204", description = "Book deleted successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "Cannot delete - copies are borrowed")
    public Response deleteBook(@PathParam("id") Long id) {
        Book book = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        long available = bookRepository.countAvailableCopies(id);
        if (available < book.getTotalCopies()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Cannot delete book - copies are currently borrowed\"}")
                    .build();
        }

        bookRepository.delete(book);
        return Response.noContent().build();
    }

    /**
     * GET /api/books/search - Search by author and/or title
     */
    @GET
    @Path("/search")
    @Operation(summary = "Search books by author and/or title")
    @APIResponse(responseCode = "200", description = "Search results")
    public List<BookDto> searchBooks(
            @QueryParam("author") String author,
            @QueryParam("title") String title) {

        List<Book> books = bookRepository.searchByAuthorAndTitle(author, title);
        return books.stream()
                .map(Mapper::toDto)
                .toList();
    }
}