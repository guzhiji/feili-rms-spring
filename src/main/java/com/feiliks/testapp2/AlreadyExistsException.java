package com.feiliks.testapp2;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String name) {
        super(name);
    }
}
