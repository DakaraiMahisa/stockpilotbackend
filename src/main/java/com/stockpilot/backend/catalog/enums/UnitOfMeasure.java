package com.stockpilot.backend.catalog.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnitOfMeasure {

    PCS("Pieces"),
    KG("Kilograms"),
    G("Grams"),
    LTR("Litres"),
    ML("Millilitres"),
    BOX("Box"),
    PACK("Pack"),
    ROLL("Roll"),
    MTR("Metres");

    private final String displayName;
}