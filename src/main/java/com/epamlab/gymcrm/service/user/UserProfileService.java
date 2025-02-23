package com.epamlab.gymcrm.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();
    private final Set<String> existingUsernames = new HashSet<>();

    public String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        String password = sb.toString();
        logger.info("Generated password: {}", password);
        return password;
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int serial = 0;

        while (existingUsernames.contains(username)) {
            serial++;
            username = baseUsername + serial;
        }

        existingUsernames.add(username);
        logger.info("Generated unique username: {}", username);
        return username;
    }
}
