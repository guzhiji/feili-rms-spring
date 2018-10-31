package com.feiliks.common

import org.apache.commons.codec.digest.DigestUtils

object PasswordUtil {

    fun hash(username: String, plainPassword: String): String {
        return DigestUtils.sha256Hex(
                DigestUtils.sha256Hex(username + plainPassword))
    }

}
