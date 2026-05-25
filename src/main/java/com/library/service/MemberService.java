package com.library.service;

import com.library.dto.MemberCreateDto;
import com.library.dto.MemberResponseDto;
import com.library.model.Member;
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

    @Transactional
    public MemberResponseDto createMember(MemberCreateDto dto) {
        if (memberRepository.existsByEmail(dto.email())) {
            throw new WebApplicationException("Email already exists", Response.Status.CONFLICT);
        }

        Member member = new Member();
        member.setFirstName(dto.firstName());
        member.setLastName(dto.lastName());
        member.setEmail(dto.email());

        memberRepository.persist(member);

        return MemberResponseDto.fromEntity(member);
    }

    public List<MemberResponseDto> listMembers() {
        List<Member> members = memberRepository.listAll();
        return members.stream().map(MemberResponseDto::fromEntity).toList();
    }

    public MemberResponseDto getMember(UUID id) {
        Member member = memberRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Member not found", Response.Status.NOT_FOUND));
        return MemberResponseDto.fromEntity(member);
    }
}