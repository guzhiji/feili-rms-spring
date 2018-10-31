package com.feiliks.blog.dtos

import com.feiliks.blog.entities.UserEntity
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class UserDto() {

    var id: Long? = null

    @NotNull(message = "Username must not be null.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{3,64}$", message = "Username is invalid.")
    lateinit var username: String

    @Pattern(regexp = "^[ \\-+0-9]{3,16}$", message = "Phone number is invalid.")
    var phone: String? = null

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    @Size(max = 128, message = "E-mail address must not be longer than 128.")
    var email: String? = null

    constructor(user: UserEntity) : this() {
        id = user.id
        username = user.username
        phone = user.phone
        email = user.email
    }

    fun toEntity(): UserEntity {
        val e = UserEntity()
        e.id = id
        e.username = username
        e.phone = phone
        e.email = email
        return e
    }

}
