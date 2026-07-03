package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.org.enums.BranchType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateBranchRequest(

        @NotBlank(message = "Branch name is required")
        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Branch code is required")
        @Size(max = 10, message = "Branch code must not exceed 10 characters")
        @Pattern(
                regexp = "^[A-Z0-9]+$",
                message = "Branch code must contain only uppercase letters and numbers"
        )
        String code,

        BranchType branchType,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phone,

        @Email(message = "Invalid email address")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @Size(max = 200, message = "Address must not exceed 200 characters")
        String addressLine1,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        UUID managerId

) {
}
