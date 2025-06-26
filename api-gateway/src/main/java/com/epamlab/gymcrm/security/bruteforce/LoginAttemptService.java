package com.epamlab.gymcrm.security.bruteforce;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000; // lockout duration after max attempts (5 minutes)

    private static class Attempt {
        int attempts;
        Instant lastFailed;
    }

    private final Map<String, Attempt> attemptsMap = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        Attempt record = attemptsMap.computeIfAbsent(username, u -> new Attempt());
        record.attempts++;
        record.lastFailed = Instant.now();
    }

    public void loginSucceeded(String username) {
        attemptsMap.remove(username);
    }

    public boolean isBlocked(String username) {
        Attempt record = attemptsMap.get(username);
        if (record == null) return false;

        if (record.attempts >= MAX_ATTEMPTS) {
            long elapsed = Instant.now().toEpochMilli() - record.lastFailed.toEpochMilli();
            if (elapsed < BLOCK_DURATION_MS) {
                return true;
            } else {
                // reset after block expires
                attemptsMap.remove(username);
            }
        }

        return false;
    }
}
