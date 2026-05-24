package com.library.service;

import com.library.dto.CheckoutRequestDto;
import com.library.dto.CheckoutResponseDto;
import com.library.model.Book;
import com.library.model.Checkout;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.CheckoutRepository;
import com.library.repository.MemberRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CheckoutService {

    @Inject
    CheckoutRepository checkoutRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    MemberRepository memberRepository;

    /**
     * Executes the checkout logic.
     * Throws WebApplicationException for business rule violations.
     */
    @Transactional
    public CheckoutResponseDto checkoutBook(CheckoutRequestDto dto) {
        Book book = bookRepository.findByIdOptional(dto.bookId())
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        Member member = memberRepository.findByIdOptional(dto.memberId()).
                orElseThrow(() -> new WebApplicationException("Member not found", Response.Status.NOT_FOUND));

        // Each member can have a maximum of 3 books checked out at a time.
        long activeCheckouts = checkoutRepository.countActiveCheckoutsByMember(dto.memberId());
        if (activeCheckouts >= 3) {
            throw new WebApplicationException("Member has reached the maximum of 3 active checkouts", Response.Status.CONFLICT);
        }

        // At least one book must be available at all times.
        long availableCopies = bookRepository.countAvailableCopies(dto.bookId());
        if (availableCopies <= 1) {
            throw new WebApplicationException("No copies available for checkout", Response.Status.CONFLICT);
        }

        Checkout checkout = new Checkout();
        checkout.setBook(book);
        checkout.setMember(member);
        checkout.setCheckoutDate(LocalDate.now());
        checkout.setDueDate(LocalDate.now().plusDays(14)); // Hardcoded 14 days
        checkout.setReturned(false);

        checkoutRepository.persist(checkout);

        return CheckoutResponseDto.fromEntity(checkout);
    }

    /**
     * Executes the return logic.
     */
    @Transactional
    public CheckoutResponseDto returnBook(java.util.UUID id) {
        Checkout checkout = checkoutRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Checkout not found", Response.Status.NOT_FOUND));

        if (checkout.isReturned()) {
            throw new WebApplicationException("Book already returned", Response.Status.CONFLICT);
        }

        checkout.setReturned(true);
        checkoutRepository.persist(checkout);

        return CheckoutResponseDto.fromEntity(checkout);
    }

    /**
     * Fetches active checkouts for a member.
     */
    public List<CheckoutResponseDto> getActiveCheckouts(UUID memberId) {
        if (memberId == null) {
            throw new WebApplicationException("memberId is required", Response.Status.BAD_REQUEST);
        }
        List<Checkout> checkouts = checkoutRepository.findActiveCheckoutsByMember(memberId);
        return checkouts.stream()
                .map(CheckoutResponseDto::fromEntity)
                .toList();
    }
}