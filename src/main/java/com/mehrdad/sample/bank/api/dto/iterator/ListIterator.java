package com.mehrdad.sample.bank.api.dto.iterator;

import java.util.List;

public class ListIterator<T> implements Iterator<T> {

    List<T> items;
    int position = 0;

    public ListIterator(List<T> items) {
        this.items = items;
    }

    @Override
    public Boolean hasNext() {
        if (items.isEmpty()|| items.get(position) == null || position >= items.size()) {
            return false;
        } else{
            position++;
            return true;}
    }

    @Override
    public T next() {
        return items.get(position);
    }
}
