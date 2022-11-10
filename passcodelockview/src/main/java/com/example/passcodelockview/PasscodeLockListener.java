package com.example.passcodelockview;

public interface PasscodeLockListener {
    void onComplete(String pin);
    void onEmpty();
    void onPinChange(int pinLength, String intermediatePin);
}
