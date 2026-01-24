package com.mehrdad.sample.bank.core.entity;

/**
 * Created by Mehrdad Ghaderi
 */
public enum Currency {

    USD,
    CAD,
    EUR

   /* USD("USD", "$"),
    CAD("CAD", "$"),
    EUR("EUR", "€");

    private final String code;   // ISO-4217
    private final String symbol;

    Currency(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Currency fromCode(String code) {
        return Currency.valueOf(code);
    }*/

    /*USD("$"), CAD("$"), EURO("€");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    private static final Map<String, Currency> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<Currency> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));

    }*/
}
