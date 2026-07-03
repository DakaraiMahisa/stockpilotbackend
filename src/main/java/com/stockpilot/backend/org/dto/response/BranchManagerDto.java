package com.stockpilot.backend.org.dto.response;


import java.util.UUID;

public record BranchManagerDto(

        UUID id,

        String firstName,

        String lastName

) {
}
