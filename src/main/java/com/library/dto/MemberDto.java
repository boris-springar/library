package com.library.dto;

import java.util.UUID;

public record MemberDto(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
    public static MemberDto fromEntity(com.library.model.Member member) {
        return new MemberDto(member.id, member.getFirstName(), member.getLastName(), member.getEmail());
    }
}