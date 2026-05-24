package com.library.resource;

import com.library.dto.BookCreateDto;
import com.library.dto.CheckoutRequestDto;
import com.library.dto.MemberCreateDto;
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
    private UUID memberId; // Changed from Long to UUID

    private String uniqueIsbn() {
        return "978-X-" + System.currentTimeMillis();
    }

    @BeforeEach
    void setup() {
        // 1. Create a unique Member
        String uniqueEmail = "member-" + System.currentTimeMillis() + "@test.com";

        memberId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MemberCreateDto("Test", "User", uniqueEmail))
                .when()
                .post("/api/members")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
        );

        // 2. Create a Book
        String uniqueIsbn = uniqueIsbn();
        bookId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BookCreateDto("Test Book", "Test Author", uniqueIsbn, 2024, 2))
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
                .body(new CheckoutRequestDto(bookId, memberId)) // Now passes UUID
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(201)
                .body("bookId", equalTo(bookId.toString()))
                .body("returned", is(false));
    }

    @Test
    void testMaxCheckoutsRule() {
        // Create 3 books and checkout each
        for (int i = 0; i < 3; i++) {
            String uniqueIsbn = uniqueIsbn();
            UUID newBookId = UUID.fromString(given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new BookCreateDto("Book " + i, "Author", uniqueIsbn, 2024, 2))
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
        // Checkout the only copy
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CheckoutRequestDto(bookId, memberId))
                .when()
                .post("/api/checkout")
                .then()
                .statusCode(201);

        // Create a DIFFERENT member for the second attempt
        String uniqueEmail2 = "member2-" + System.currentTimeMillis() + "@test.com";
        UUID differentMemberId = UUID.fromString(given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MemberCreateDto("Other", "User", uniqueEmail2))
                .when()
                .post("/api/members")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
        );

        // Try again with the different member
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