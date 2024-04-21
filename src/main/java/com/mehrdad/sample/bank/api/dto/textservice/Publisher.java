package com.mehrdad.sample.bank.api.dto.textservice;


public interface Publisher {
    void registerListener(Listener listener);

    void removeListener(Listener listener);

    void notifyListeners(Event event);
}
