package com.library.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CheckoutResponseDto(
        UUID id,
        UUID bookId,
        String bookTitle,
        UUID memberId,
        LocalDate checkoutDate,
        LocalDate dueDate,
        boolean returned
) {
    public static CheckoutResponseDto fromEntity(com.library.model.Checkout checkout) {
        return new CheckoutResponseDto(
                checkout.id,
                checkout.getBook().id,
                checkout.getBook().getTitle(),
                checkout.getMember().id,
                checkout.getCheckoutDate(),
                checkout.getDueDate(),
                checkout.isReturned()
        );
    }

}