package com.library.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequestDto(
        @NotNull(message = "Book ID is required")
        Long bookId,

        @NotNull(message = "Member ID is required")
        Long memberId
) {}