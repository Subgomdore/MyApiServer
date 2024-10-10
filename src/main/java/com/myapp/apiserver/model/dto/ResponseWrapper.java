package com.myapp.apiserver.model.dto;

public class ResponseWrapper<T> {
    private String status;
    private String message;
    private T data;

    public ResponseWrapper(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
