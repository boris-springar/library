package com.library.resource;

import com.library.dto.BookCreateDto;
import com.library.dto.CheckoutRequestDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class CheckoutResourceTest {

    private UUID bookId;
    private Long memberId = 1L;

    private String uniqueIsbn() {
        return "978-X-" + System.currentTimeMillis();
    }

    @BeforeEach
    void setup() {
        memberId = 100L + System.currentTimeMillis();

        String uniqueIsbn = uniqueIsbn();

        bookId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BookCreateDto("Test Book", "Test Author", uniqueIsbn, 2024, 1))
                .when()
                .post("/api/books")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
        );
    }

    @Test
    void testSuccessfulCheckout() {
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, memberId))
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(201)
                .body("bookId", equalTo(bookId.toString()))
                .body("returned", is(false));
    }

    @Test
    void testMaxLoansRule() {
        for (int i = 0; i < 3; i++) {
            String uniqueIsbn = uniqueIsbn();
            UUID newBookId = UUID.fromString(given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new BookCreateDto("Book " + i, "Author", uniqueIsbn, 2024, 1))
                    .when()
                    .post("/api/books")
                    .then()
                    .statusCode(201)
                    .extract()
                    .path("id")
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CheckoutRequestDto(newBookId, memberId))
                    .when()
                    .post("/api/checkout")
                    .then()
                    .statusCode(201);
        }

        // Try 4th book
        String extraIsbn = uniqueIsbn();
        UUID extraBookId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BookCreateDto("Extra Book", "Author", extraIsbn, 2024, 1))
                .when()
                .post("/api/books")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
        );

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(extraBookId, memberId))
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(409)
                .body("error", containsString("maximum of 3"));
    }

    @Test
    void testNoCopiesAvailable() {
        // memberId is unique, so no previous checkouts
        // Checkout the only copy
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, memberId))
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(201);

        // Try again with a different member (also unique)
        Long differentMemberId = memberId + 1;
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, differentMemberId))
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(409)
                .body("error", containsString("No copies available"));
    }
}