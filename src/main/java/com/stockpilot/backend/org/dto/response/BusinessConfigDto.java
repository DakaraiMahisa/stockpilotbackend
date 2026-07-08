package com.stockpilot.backend.org.dto.response;

import com.stockpilot.backend.org.enums.CurrencyPosition;
import com.stockpilot.backend.org.enums.DimensionUnit;
import com.stockpilot.backend.org.enums.NumberFormat;
import com.stockpilot.backend.org.enums.TimeFormat;
import com.stockpilot.backend.org.enums.WeightUnit;

import java.util.UUID;

public record BusinessConfigDto(

        UUID id,

        String timezone,

        String currencyCode,

        String currencySymbol,

        CurrencyPosition currencyPosition,

        String dateFormat,

        TimeFormat timeFormat,

        NumberFormat numberFormat,

        Integer decimalPlaces,

        String fiscalYearStart,

        String defaultLanguage,

        WeightUnit weightUnit,

        DimensionUnit dimensionUnit
) {
}