package com.example.ocrtest;

public interface TextRecognitionCallback {
    void onSuccess(String text);
    void onFailure(Exception e);
}
