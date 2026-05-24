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

    @BeforeEach
    void setup() {
        bookId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BookCreateDto("Test Book", "Test Author", "ISBN-TEST-001", 2024, 1))
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
                .post("/api/loans")
                .then()
                .statusCode(201)
                .body("bookId", equalTo(bookId))
                .body("returned", is(false));
    }

    @Test
    void testMaxLoansRule() {
        // Create 3 more books
        for (int i = 0; i < 3; i++) {
            UUID newBookId = given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new BookCreateDto("Book " + i, "Author", "ISBN-" + i, 2024, 1))
                    .when()
                    .post("/api/books")
                    .then()
                    .statusCode(201)
                    .extract()
                    .path("id");

            // Checkout each
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CheckoutRequestDto(newBookId, memberId))
                    .when()
                    .post("/api/loans")
                    .then()
                    .statusCode(201);
        }

        // Try 4th
        UUID extraBookId = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BookCreateDto("Extra Book", "Author", "ISBN-EXTRA", 2024, 1))
                .when()
                .post("/api/books")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(extraBookId, memberId))
                .when()
                .post("/api/loans")
                .then()
                .statusCode(409)
                .body("error", containsString("maximum of 3"));
    }

    @Test
    void testNoCopiesAvailable() {
        // Checkout the only copy
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, memberId))
                .when()
                .post("/api/loans")
                .then()
                .statusCode(201);

        // Try again
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, memberId + 1))
                .when()
                .post("/api/loans")
                .then()
                .statusCode(409)
                .body("error", containsString("No copies available"));
    }
}