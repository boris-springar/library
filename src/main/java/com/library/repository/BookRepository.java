package com.library.repository;

import com.library.model.Book;
import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class BookRepository implements PanacheRepositoryBase<Book, UUID> {
    /**
     * Checks if a book with the given ISBN already exists.
     */
    public boolean existsByIsbn(String isbn) {
        return find("isbn", isbn).count() > 0;
    }

    /**
     * Finds a book by ISBN.
     */
    public Optional<Book> findByIsbn(String isbn) {
        return find("isbn", isbn).firstResultOptional();
    }

    /**
     * Searches books by author and/or title.
     * Uses dynamic query construction for flexibility.
     */
    public List<Book> searchByAuthorAndTitle(String author, String title) {
        StringBuilder query = new StringBuilder("select b from Book b where 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (author != null && !author.isBlank()) {
            query.append(" and lower(b.author) like ?");
            params.add("%" + author.toLowerCase() + "%");
        }

        if (title != null && !title.isBlank()) {
            query.append(" and lower(b.title) like ?");
            params.add("%" + title.toLowerCase() + "%");
        }

        // Execute the dynamic query
        if (params.isEmpty()) {
            return listAll();
        }

        String finalQuery = "select b from Book b";
        List<String> conditions = new java.util.ArrayList<>();
        List<Object> finalParams = new java.util.ArrayList<>();

        if (author != null && !author.isBlank()) {
            conditions.add("lower(b.author) like ?1");
            finalParams.add("%" + author.toLowerCase() + "%");
        }
        if (title != null && !title.isBlank()) {
            conditions.add("lower(b.title) like ?" + (conditions.size() + 1));
            finalParams.add("%" + title.toLowerCase() + "%");
        }

        if (!conditions.isEmpty()) {
            finalQuery += " where " + String.join(" and ", conditions);
        }

        return list(finalQuery, finalParams.toArray());
    }

    /**
     * Calculates the number of available copies for a specific book.
     * Available = Total Copies - (Active Loans for this book)
     */
    public long countAvailableCopies(UUID bookId) { // Changed from Long to String
        Book book = findByIdOptional(bookId).orElse(null);
        if (book == null) return 0;

        // Update the query to use the String ID
        long borrowed = Checkout.count("book.id = ?1 and returned = false", bookId);
        return book.getTotalCopies() - borrowed;
    }
}