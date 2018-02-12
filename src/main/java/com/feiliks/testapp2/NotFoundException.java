package com.feiliks.testapp2;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String name) {
        super(name);
    }
}
