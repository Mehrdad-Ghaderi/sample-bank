package com.mehrdad.sample.bank.view;

public interface UIState {
    UIState run(UIState previousState);
}
