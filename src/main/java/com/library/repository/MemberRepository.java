package com.library.repository;

import com.library.model.Member;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MemberRepository implements PanacheRepositoryBase<Member, UUID> {

    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }

    public Optional<Member> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}