package com.mehrdad.sample.bank.core.entity;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum Currency {

    USD("$"), CAD("C$"), EURO("€"), RIAL("R");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    // Implementing a fromString method on an enum type
    private static final Map<String, Currency> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<Currency> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));

    }
}
