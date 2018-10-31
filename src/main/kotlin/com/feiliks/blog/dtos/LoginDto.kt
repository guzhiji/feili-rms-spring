package com.feiliks.common.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class LoginDto {

    /**
     * @param username the username to set
     * @return the username
     */
    @NotNull(message = "Please type your username.")
    @Size(min = 1, message = "Please type a valid username.")
    var username: String? = null

    /**
     * @param password the password to set
     * @return the password
     */
    @NotNull(message = "Please type your password.")
    @Size(min = 1, message = "Please type a valid password.")
    var password: String? = null

}
