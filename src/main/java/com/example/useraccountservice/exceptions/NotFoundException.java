package com.example.useraccountservice.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String ex){
        super(ex);
    }
}