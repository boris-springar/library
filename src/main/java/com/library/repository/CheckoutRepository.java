package com.library.repository;

import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CheckoutRepository implements PanacheRepositoryBase<Checkout, UUID> {

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

    public long countActiveCheckoutsByMember(UUID memberId) {
        return count("member.id = ?1 and returned = false", memberId);
    }

    public List<Checkout> findActiveCheckoutsByMember(UUID memberId) {
        return list("member.id = ?1 and returned = false", memberId);
    }

    /*public Optional<Checkout> findByIdOptional(UUID id) {
        return findByIdOptional(id);
    }*/
}