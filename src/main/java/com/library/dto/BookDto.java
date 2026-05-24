package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BookDto(
        UUID id,
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @NotBlank(message = "Author is required")
        @Size(max = 255, message = "Author cannot exceed 255 characters")
        String author,

        @NotBlank(message = "ISBN is required")
        @Size(max = 20, message = "ISBN format invalid")
        String isbn,

        @NotNull(message = "Publication year is required")
        @Min(value = 1000, message = "Invalid publication year")
        Integer publicationYear,

        @NotNull(message = "Total copies is required")
        @Min(value = 1, message = "Must have at least 1 copy")
        Integer totalCopies
) {
    // Helper to create a DTO from an entity (for responses)
    public static BookDto fromEntity(com.library.model.Book book) {
        return new BookDto(
                book.id,
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getTotalCopies()
        );
    }
}