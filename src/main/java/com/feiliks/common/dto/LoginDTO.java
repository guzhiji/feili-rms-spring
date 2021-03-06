package com.feiliks.common.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginDTO {

    @NotNull(message = "Please type your username.")
    @Size(min = 1, message = "Please type a valid username.")
    private String username;
    @NotNull(message = "Please type your password.")
    @Size(min = 1, message = "Please type a valid password.")
    private String password;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
