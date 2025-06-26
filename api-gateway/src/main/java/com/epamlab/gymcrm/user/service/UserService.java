package com.epamlab.gymcrm.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();
    private final Set<String> existingUsernames = new HashSet<>();

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String generateRawPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        String raw = sb.toString();
        logger.info("Generated raw password: {}", raw);
        return raw;
    }

    public String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    public boolean matchesRawPassword(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String base = (firstName + "." + lastName).toLowerCase();
        String username = base;
        int serial = 0;

        while (existingUsernames.contains(username)) {
            serial++;
            username = base + serial;
        }

        existingUsernames.add(username);
        logger.info("Generated username: {}", username);
        return username;
    }
}
