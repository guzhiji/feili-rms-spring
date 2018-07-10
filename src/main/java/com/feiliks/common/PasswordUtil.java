package com.feiliks.common;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtil {

    public static String hash(String username, String plainPassword) {
        return DigestUtils.sha256Hex(
                DigestUtils.sha256Hex(String.format(
                        "%s%s", username, plainPassword)));
    }

}
