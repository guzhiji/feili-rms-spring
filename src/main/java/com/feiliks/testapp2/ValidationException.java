package com.feiliks.testapp2;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationException extends RuntimeException {

    private static String createMessage(BindingResult result) {
        FieldError fe = result.getFieldError();
        if (fe == null) {
            return result.toString();
        } else {
            return fe.getDefaultMessage();
        }
    }

    public ValidationException(BindingResult result) {
        super(createMessage(result));
    }

    public ValidationException(String msg) {
        super(msg);
    }
}
