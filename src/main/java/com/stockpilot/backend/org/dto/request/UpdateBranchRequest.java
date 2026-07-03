package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateBranchRequest(

        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        String name,

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
