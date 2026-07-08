package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.org.enums.CurrencyPosition;
import com.stockpilot.backend.org.enums.DimensionUnit;
import com.stockpilot.backend.org.enums.NumberFormat;
import com.stockpilot.backend.org.enums.TimeFormat;
import com.stockpilot.backend.org.enums.WeightUnit;
import com.stockpilot.backend.shared.validation.annotation.ValidCurrencyCode;
import com.stockpilot.backend.shared.validation.annotation.ValidFiscalYear;
import com.stockpilot.backend.shared.validation.annotation.ValidTimezone;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record BusinessConfigUpdateRequest(

        @ValidTimezone
        String timezone,

        @ValidCurrencyCode
        String currencyCode,

        @Size(max = 5)
        String currencySymbol,

        CurrencyPosition currencyPosition,

        @Size(max = 20)
        String dateFormat,

        TimeFormat timeFormat,

        NumberFormat numberFormat,

        @Min(0)
        @Max(4)
        Integer decimalPlaces,

        @ValidFiscalYear
        String fiscalYearStart,

        @Size(max = 5)
        String defaultLanguage,

        WeightUnit weightUnit,

        DimensionUnit dimensionUnit
) {
}