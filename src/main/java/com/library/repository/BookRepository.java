package com.library.repository;

import com.library.model.Book;
import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {

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

        // Note: In a real production scenario, using a Query object builder is safer for dynamic params
        // For simplicity in this snippet, we'll use the find method with a fixed query structure if possible,
        // or rely on the 'find' method with named parameters if we constructed a named query.
        // However, Panache allows passing a query string and parameters directly.

        // Re-implementing with Panache's find(String query, Object... parameters)
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
    public long countAvailableCopies(Long bookId) {
        long total = findById(bookId).getTotalCopies();
        long borrowed = Checkout.count("book.id = ?1 and returned = false", bookId);
        return total - borrowed;
    }
}