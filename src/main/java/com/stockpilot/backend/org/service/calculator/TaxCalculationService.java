package com.stockpilot.backend.org.service.calculator;

import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.TaxType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class TaxCalculationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public TaxBreakdownDto calculate(
            TaxClass taxClass,
            BigDecimal taxableAmount,
            List<TaxRate> rates
    ) {

        BigDecimal vat = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;

        if (taxClass.getTaxType() == TaxType.NONE) {

            return TaxBreakdownDto.builder()
                    .taxableAmount(taxableAmount)
                    .vat(BigDecimal.ZERO)
                    .cgst(BigDecimal.ZERO)
                    .sgst(BigDecimal.ZERO)
                    .igst(BigDecimal.ZERO)
                    .totalTax(BigDecimal.ZERO)
                    .totalWithTax(taxableAmount)
                    .build();
        }

        for (TaxRate rate : rates) {

            BigDecimal taxAmount = calculateComponent(
                    taxableAmount,
                    rate.getRate()
            );

            switch (rate.getRateType()) {

                case VAT -> vat = vat.add(taxAmount);

                case CGST -> cgst = cgst.add(taxAmount);

                case SGST -> sgst = sgst.add(taxAmount);

                case IGST -> igst = igst.add(taxAmount);
            }
        }

        BigDecimal totalTax = vat
                .add(cgst)
                .add(sgst)
                .add(igst);

        return TaxBreakdownDto.builder()
                .taxableAmount(taxableAmount)
                .vat(vat)
                .cgst(cgst)
                .sgst(sgst)
                .igst(igst)
                .totalTax(totalTax)
                .totalWithTax(taxableAmount.add(totalTax))
                .build();
    }

    private BigDecimal calculateComponent(
            BigDecimal amount,
            BigDecimal percentage
    ) {

        return amount
                .multiply(percentage)
                .divide(
                        ONE_HUNDRED,
                        2,
                        RoundingMode.HALF_UP
                );
    }
}