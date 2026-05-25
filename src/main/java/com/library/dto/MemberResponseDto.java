package com.library.dto;

import java.util.UUID;

public record MemberResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
    public static MemberResponseDto fromEntity(com.library.model.Member member) {
        return new MemberResponseDto(member.id, member.getFirstName(), member.getLastName(), member.getEmail());
    }
}