package com.library.repository;

import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CheckoutRepository implements PanacheRepository<Checkout> {

    /**
     * Counts the number of active (non-returned) loans for a specific member.
     * Used to enforce the "max 3 books" rule.
     */
    public long countActiveLoansByMember(Long memberId) {
        return count("memberId = ?1 and returned = false", memberId);
    }

    /**
     * Retrieves all active loans for a specific member.
     */
    public List<Checkout> findActiveLoansByMember(Long memberId) {
        return list("memberId = ?1 and returned = false", memberId);
    }

    /**
     * Finds a specific loan by ID to process returns.
     */
    /*public Checkout findByIdOrThrow(Long id) {
        return this.<Checkout>findByIdOrFail(id);
    }*/

    public long countActiveCheckoutsByMember(Long memberId) {
        return count("memberId = ?1 and returned = false", memberId);
    }

    public List<Checkout> findActiveCheckoutsByMember(Long memberId) {
        return list("memberId = ?1 and returned = false", memberId);
    }
}