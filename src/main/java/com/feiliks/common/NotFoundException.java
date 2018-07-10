package com.feiliks.common;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String name) {
        super(name);
    }

    public NotFoundException(Class<?> entityCls, String name) {
        super(String.format("%s not found: %s", entityCls.getSimpleName(), name));
    }
}
