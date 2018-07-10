package com.feiliks.common;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String name) {
        super(name);
    }
}
