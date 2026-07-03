package com.stockpilot.backend.org.dto.request;



import com.stockpilot.backend.org.enums.BranchStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateBranchStatusRequest(

        @NotNull(message = "Branch status is required")
        BranchStatus status

) {
}
