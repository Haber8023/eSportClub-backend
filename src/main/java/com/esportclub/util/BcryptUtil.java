package com.esportclub.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;
        // 兼容老格式（未加密明文）
        if (encodedPassword.equals(rawPassword)) return true;
        return encoder.matches(rawPassword, encodedPassword);
    }
}
