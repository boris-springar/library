package com.library.resource;

import com.library.dto.BookCreateDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BookResourceTest {

    @Test
    void testCreateBook() {
        BookCreateDto dto = new BookCreateDto(
                "Dune", "Frank Herbert", "978-0441172719", 1965, 2
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
        BookCreateDto dto = new BookCreateDto(
                "Another Book", "Author", "978-0441172719", 2020, 1
        );

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
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
                .body("content", notNullValue());
    }
}