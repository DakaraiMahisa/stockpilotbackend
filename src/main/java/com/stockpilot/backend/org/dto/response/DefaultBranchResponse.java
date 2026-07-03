package com.stockpilot.backend.org.dto.response;

public record DefaultBranchResponse(

        BranchDto previousDefault,

        BranchDto newDefault

) {
}
