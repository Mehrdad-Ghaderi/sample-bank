package com.mehrdad.sample.bank.api.dto.textservice;

public class Event {
    private String text;

    public Event(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}



