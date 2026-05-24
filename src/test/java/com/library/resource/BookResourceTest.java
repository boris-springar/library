package com.library.resource;

import com.library.dto.BookCreateDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BookResourceTest {

    // Helper to generate unique ISBNs
    private String uniqueIsbn() {
        return "978-X-" + System.currentTimeMillis();
    }

    @Test
    void testCreateBook() {
        String isbn = uniqueIsbn(); // Unique ISBN
        BookCreateDto dto = new BookCreateDto(
                "Dune", "Frank Herbert", isbn, 1965, 2
        );

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .when()
                .post("/api/books")
                .then()
                .statusCode(201)
                .body("title", equalTo("Dune"))
                .body("id", notNullValue());
    }

    @Test
    void testDuplicateISBN() {
        String isbn = uniqueIsbn(); // Unique ISBN for the FIRST book

        // 1. Create the first book
        BookCreateDto first = new BookCreateDto(
                "First Book", "Author", isbn, 2020, 1
        );
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(first)
                .when()
                .post("/api/books")
                .then()
                .statusCode(201);

        // 2. Try to create a DUPLICATE with the SAME ISBN
        BookCreateDto duplicate = new BookCreateDto(
                "Duplicate Book", "Author", isbn, 2021, 1
        );
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(duplicate)
                .when()
                .post("/api/books")
                .then()
                .statusCode(409)
                .body("error", containsString("ISBN"));
    }

    @Test
    void testGetBooks() {
        given()
                .when()
                .get("/api/books")
                .then()
                .statusCode(200)
                .body("content", notNullValue()); // Should return empty list or list with books from other tests
    }
}