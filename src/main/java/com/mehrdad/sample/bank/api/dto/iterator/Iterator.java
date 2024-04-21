package com.mehrdad.sample.bank.api.dto.iterator;

public interface Iterator<T> {
    Boolean hasNext();

    T next();
}
