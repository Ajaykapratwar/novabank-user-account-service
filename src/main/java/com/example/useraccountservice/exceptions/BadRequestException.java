package com.example.useraccountservice.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String ex){
        super(ex);
    }
}