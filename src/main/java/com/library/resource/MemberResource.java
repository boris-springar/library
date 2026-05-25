package com.library.resource;

import com.library.dto.MemberCreateDto;
import com.library.dto.MemberResponseDto;
import com.library.service.MemberService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/api/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Members", description = "Member management endpoints")
public class MemberResource {
    @Inject
    MemberService memberService;

    @POST
    @Transactional
    @Operation(summary = "Register a new member")
    @APIResponse(responseCode = "201", description = "Member created")
    @APIResponse(responseCode = "409", description = "Email already exists")
    public Response createMember(MemberCreateDto dto) {
        MemberResponseDto result = memberService.createMember(dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Operation(summary = "List all members")
    @APIResponse(responseCode = "200", description = "List of members")
    public List<MemberResponseDto> listMembers() {
        return memberService.listMembers();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a specific member")
    @APIResponse(responseCode = "200", description = "Member found")
    @APIResponse(responseCode = "404", description = "Member not found")
    public Response getMember(@PathParam("id") UUID id) {
        return Response.ok(memberService.getMember(id)).build();
    }
}