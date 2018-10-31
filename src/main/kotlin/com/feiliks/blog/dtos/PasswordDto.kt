package com.feiliks.common.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class PasswordDto {

    @NotNull(message = "Please enter original password.")
    var original: String? = null

    @NotNull(message = "Password must not be null.")
    @Size(min = 6, max = 128, message = "Length of password should be between 6 to 128.")
    var password: String? = null

}
