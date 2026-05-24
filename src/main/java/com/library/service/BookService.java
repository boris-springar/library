package com.library.service;

import com.library.dto.BookCreateDto;
import com.library.dto.BookDto;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.util.Mapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.panache.common.Page;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookService {

    @Inject
    BookRepository bookRepository;

    /**
     * Creates a new book.
     * Throws WebApplicationException if ISBN already exists.
     */
    @Transactional
    public BookDto createBook(BookCreateDto dto) {
        if (bookRepository.existsByIsbn(dto.isbn())) {
            throw new WebApplicationException("ISBN already exists", Response.Status.CONFLICT);
        }

        Book book = Mapper.toEntity(dto);
        bookRepository.persist(book);

        return Mapper.toDto(book);
    }

    /**
     * Lists books with pagination.
     * Validates page and size parameters.
     */
    public List<BookDto> listBooks(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new BadRequestException("Invalid pagination parameters");
        }

        List<Book> books = bookRepository.findAll()
                .page(Page.of(page, size))
                .list();

        return books.stream()
                .map(Mapper::toDto)
                .toList();
    }

    /**
     * Gets a single book by ID.
     * Throws WebApplicationException if not found.
     */
    public BookDto getBook(UUID id) {
        Book book = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));
        return Mapper.toDto(book);
    }

    /**
     * Updates an existing book.
     * Checks for ISBN conflicts if the ISBN is changed.
     */
    @Transactional
    public BookDto updateBook(UUID id, BookDto dto) {
        Book existing = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        // Check if ISBN is being changed and if the new ISBN exists
        if (!existing.getIsbn().equals(dto.isbn()) && bookRepository.existsByIsbn(dto.isbn())) {
            throw new WebApplicationException("ISBN already exists", Response.Status.CONFLICT);
        }

        existing.setTitle(dto.title());
        existing.setAuthor(dto.author());
        existing.setIsbn(dto.isbn());
        existing.setPublicationYear(dto.publicationYear());
        existing.setTotalCopies(dto.totalCopies());

        bookRepository.persist(existing);

        return Mapper.toDto(existing);
    }

    /**
     * Deletes a book.
     * Throws WebApplicationException if copies are currently borrowed.
     */
    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        long available = bookRepository.countAvailableCopies(id);
        if (available < book.getTotalCopies()) {
            throw new WebApplicationException("Cannot delete book - copies are currently borrowed", Response.Status.CONFLICT);
        }

        bookRepository.delete(book);
    }

    /**
     * Searches books by author and/or title.
     */
    public List<BookDto> searchBooks(String author, String title) {
        List<Book> books = bookRepository.searchByAuthorAndTitle(author, title);
        return books.stream()
                .map(Mapper::toDto)
                .toList();
    }
}