package com.library.repository;

import com.library.model.Checkout;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CheckoutRepository implements PanacheRepositoryBase<Checkout, UUID> {
    public long countActiveCheckoutsByMember(UUID memberId) {
        return count("member.id = ?1 and returned = false", memberId);
    }

    public List<Checkout> findActiveCheckoutsByMember(UUID memberId) {
        return list("member.id = ?1 and returned = false", memberId);
    }
}