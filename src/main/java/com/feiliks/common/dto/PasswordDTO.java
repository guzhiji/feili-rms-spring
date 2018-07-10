package com.feiliks.common.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordDTO {

    private String original;
    @NotNull(message = "Password must not be null.")
    @Size(min = 6, max = 128, message = "Length of password should be between 6 to 128.")
    private String password;

    public void setOriginal(String password) {
        original = password;
    }

    public String getOriginal() {
        return original;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

}
