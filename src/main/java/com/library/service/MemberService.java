package com.library.service;

import com.library.dto.MemberCreateDto;
import com.library.dto.MemberDto;
import com.library.model.Member;
import com.library.repository.CheckoutRepository;
import com.library.repository.MemberRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MemberService {

    @Inject
    MemberRepository memberRepository;

    @Inject
    CheckoutRepository checkoutRepository;

    @Transactional
    public MemberDto createMember(MemberCreateDto dto) {
        if (memberRepository.existsByEmail(dto.email())) {
            throw new WebApplicationException("Email already exists", Response.Status.CONFLICT);
        }

        Member member = new Member();
        member.setFirstName(dto.firstName());
        member.setLastName(dto.lastName());
        member.setEmail(dto.email());

        memberRepository.persist(member);

        return MemberDto.fromEntity(member);
    }

    public List<MemberDto> listMembers() {
        List<Member> members = memberRepository.listAll();
        return members.stream().map(m -> {
            long activeLoans = checkoutRepository.countActiveCheckoutsByMember(m.getId());
            return MemberDto.fromEntity(m);
        }).toList();
    }

    public MemberDto getMember(UUID id) {
        Member member = memberRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Member not found", Response.Status.NOT_FOUND));
        long activeLoans = checkoutRepository.countActiveCheckoutsByMember(id);
        return MemberDto.fromEntity(member);
    }
}