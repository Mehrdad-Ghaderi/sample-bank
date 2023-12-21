package com.mehrdad.sample.bank.api.dto.textservice;


public interface Publisher {
    void registerListener(EventListener eventListener);

    void removeListener(EventListener eventListener);

    void notifyListeners();
}
