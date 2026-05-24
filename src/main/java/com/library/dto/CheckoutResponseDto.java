package com.library.dto;

import java.time.LocalDate;

public record CheckoutResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        Long memberId,
        LocalDate loanDate,
        LocalDate dueDate,
        boolean returned
) {
    public static CheckoutResponseDto fromEntity(com.library.model.Checkout checkout) {
        return new CheckoutResponseDto(
                checkout.id,
                checkout.getBook().id,
                checkout.getBook().getTitle(),
                checkout.getMemberId(),
                checkout.getLoanDate(),
                checkout.getDueDate(),
                checkout.isReturned()
        );
    }

}