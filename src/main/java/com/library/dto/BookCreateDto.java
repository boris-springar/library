package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// No 'id' field here
public record BookCreateDto(
        @NotBlank(message = "Title is required")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Author is required")
        @Size(max = 255)
        String author,

        @NotBlank(message = "ISBN is required")
        @Size(max = 20)
        String isbn,

        @NotNull(message = "Publication year is required")
        @Min(1000)
        Integer publicationYear,

        @NotNull(message = "Total copies is required")
        @Min(1)
        Integer totalCopies
) {}