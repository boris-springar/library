package com.library.repository;

import com.library.model.Book;
import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
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
     * Searches books by author and/or title.
     * Uses dynamic query construction for flexibility.
     */
    public List<Book> searchByAuthorAndTitle(String author, String title) {
        if ((author == null || !author.isBlank()) && (title == null || title.isBlank())) {
            return listAll();
        }

        String query = "select b from Book b";
        List<String> conditions = new java.util.ArrayList<>();
        List<Object> params = new java.util.ArrayList<>();

        if (author != null && !author.isBlank()) {
            conditions.add("lower(b.author) like ?1");
            params.add("%" + author.toLowerCase() + "%");
        }
        if (title != null && !title.isBlank()) {
            conditions.add("lower(b.title) like ?" + (conditions.size() + 1));
            params.add("%" + title.toLowerCase() + "%");
        }

        if (!conditions.isEmpty()) {
            query += " where " + String.join(" and ", conditions);
        }

        return list(query, params.toArray());
    }

    /**
     * Calculates the number of available copies for a specific book.
     * Available = Total Copies - (Active checkouts for this book)
     */
    public long countAvailableCopies(UUID bookId) {
        Book book = findByIdOptional(bookId).orElse(null);
        if (book == null) return 0;

        // Update the query to use the String ID
        long borrowed = Checkout.count("book.id = ?1 and returned = false", bookId);
        return book.getTotalCopies() - borrowed;
    }
}