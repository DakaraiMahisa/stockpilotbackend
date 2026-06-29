package com.stockpilot.backend.shared.storage;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;


@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    @NotBlank
    private String bucket;

    @Valid
    private Folders folders = new Folders();

    @Valid
    private Upload upload = new Upload();

    @Getter
    @Setter
    public static class Folders {

        private String organizationLogos = "org-logos";
        private String productImages = "product-images";
        private String documents = "documents";
    }

    @Getter
    @Setter
    public static class Upload {

        @Min(60)
        private long presignedUrlExpirySeconds = 300;

        @Min(1024)
        private long maxLogoSizeBytes = 2 * 1024 * 1024;
    }

    private Set<String> allowedContentTypes = Set.of(
            "image/png",
            "image/jpeg"
    );
}