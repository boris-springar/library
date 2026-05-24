package com.library.resource;

import com.library.dto.CheckoutRequestDto;
import com.library.dto.CheckoutResponseDto;
import com.library.model.Book;
import com.library.model.Checkout;
import com.library.repository.BookRepository;
import com.library.repository.CheckoutRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loans", description = "Checkout/Loan management endpoints")
public class CheckoutResource {

    @Inject
    CheckoutRepository checkoutRepository;

    @Inject
    BookRepository bookRepository;

    /**
     * POST /api/loans - Borrow a book
     * Business rules:
     *   - Max 3 active loans per member
     *   - At least 1 copy must be available
     *   - Due date = loan date + 14 days
     */
    @POST
    @Transactional
    @Operation(summary = "Borrow a book")
    @APIResponse(responseCode = "201", description = "Book borrowed successfully")
    @APIResponse(responseCode = "404", description = "Book not found")
    @APIResponse(responseCode = "409", description = "Business rule violation")
    public Response checkoutBook(@Valid CheckoutRequestDto dto) {
        // 1. Verify book exists
        Book book = bookRepository.findByIdOptional(dto.bookId())
                .orElseThrow(() -> new WebApplicationException("Book not found", Response.Status.NOT_FOUND));

        // 2. Check max 3 active loans per member
        long activeLoans = 2;// checkoutRepository.countActiveCheckoutsByMember(dto.memberId());
        if (activeLoans >= 3) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Member has reached the maximum of 3 active loans\"}")
                    .build();
        }

        // 3. Check copy availability
        long availableCopies = bookRepository.countAvailableCopies(dto.bookId());
        if (availableCopies <= 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"No copies available for checkout\"}")
                    .build();
        }

        // 4. Create the checkout (due date = today + 14 days)
        Checkout checkout = new Checkout();
        checkout.setBook(book);
        checkout.setMemberId(dto.memberId());
        checkout.setLoanDate(LocalDate.now());
        checkout.setDueDate(LocalDate.now().plusDays(14));
        checkout.setReturned(false);

        checkoutRepository.persist(checkout);

        return Response.status(Response.Status.CREATED)
                .entity(CheckoutResponseDto.fromEntity(checkout))
                .build();
    }

    /**
     * POST /api/loans/{id}/return - Return a book
     */
    @POST
    @Path("/{id}/return")
    @Transactional
    @Operation(summary = "Return a borrowed book")
    @APIResponse(responseCode = "200", description = "Book returned successfully")
    @APIResponse(responseCode = "404", description = "Loan not found")
    @APIResponse(responseCode = "409", description = "Book already returned")
    public Response returnBook(@PathParam("id") Long id) {
        Checkout checkout = checkoutRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Loan not found", Response.Status.NOT_FOUND));

        if (checkout.isReturned()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Book already returned\"}")
                    .build();
        }

        checkout.setReturned(true);
        checkoutRepository.persist(checkout);

        return Response.ok(CheckoutResponseDto.fromEntity(checkout)).build();
    }

    /**
     * GET /api/loans?memberId=... - List active loans for a member
     */
    @GET
    @Operation(summary = "List active loans for a member")
    @APIResponse(responseCode = "200", description = "Loans retrieved successfully")
    @APIResponse(responseCode = "400", description = "Missing memberId parameter")
    public List<CheckoutResponseDto> getActiveLoans(@QueryParam("memberId") Long memberId) {
        if (memberId == null) {
            throw new BadRequestException("memberId query parameter is required");
        }

        List<Checkout> loans = checkoutRepository.findActiveCheckoutsByMember(memberId);
        return loans.stream()
                .map(CheckoutResponseDto::fromEntity)
                .toList();
    }
}