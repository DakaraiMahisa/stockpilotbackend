package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.common.utils.TestConstants;
import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.enums.TaxType;

import java.util.List;
import java.util.UUID;

public final class TaxClassBuilders {

    private TaxClassBuilders() {
    }

    public static TaxClass.TaxClassBuilder aTaxClass() {
        return TaxClass.builder()
                .id(TestConstants.TAX_CLASS_ID)
                .tenantId(TestConstants.TENANT_ID)
                .name("GST 18%")
                .code("GST18")
                .taxType(TaxType.GST)
                .defaultTaxClass(true)
                .description("Standard GST rate");
    }

    public static CreateTaxClassRequest createTaxClassRequest() {
        return new CreateTaxClassRequest(
                "GST 18%",
                "GST18",
                TaxType.GST,
                true,
                null,
                "Standard GST rate",
                List.of(
                        TaxRateBuilders.createCgstRateRequest(),
                        TaxRateBuilders.createSgstRateRequest()
                )
        );
    }

    public static UpdateTaxClassRequest updateTaxClassRequest() {
        return new UpdateTaxClassRequest(
                "GST 18% Updated",
                "Updated description",
                null
        );
    }

    public static TaxClassDto taxClassDto() {
        return new TaxClassDto(
                UUID.randomUUID(),
                "GST 18%",
                "GST18",
                TaxType.GST,
                true,
                null,
                "Standard GST rate",
                List.of(
                        TaxRateBuilders.taxRateDto()
                )
        );
    }
}
