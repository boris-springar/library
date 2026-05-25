package com.library.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutCreateDto(
        @NotNull(message = "Book ID is required")
        UUID bookId,

        @NotNull(message = "Member ID is required")
        UUID memberId
) {}