package com.mehrdad.sample.bank.view;

/**
 * Created by Mehrdad Ghaderi
 */
public interface UIState {
    UIState run(UIState previousState);
}
