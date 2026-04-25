package com.mehrdad.sample.bank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mehrdad.sample.bank.security.exception.TooManyLoginAttemptsException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final int maxFailedAttempts;
    private final Duration lockDuration;
    private final Clock clock;

    @Autowired
    public LoginAttemptService(
            @Value("${app.security.login-rate-limit.max-failed-attempts:5}") int maxFailedAttempts,
            @Value("${app.security.login-rate-limit.lock-seconds:900}") long lockSeconds
    ) {
        this(maxFailedAttempts, Duration.ofSeconds(lockSeconds), Clock.systemUTC());
    }

    LoginAttemptService(int maxFailedAttempts, Duration lockDuration, Clock clock) {
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockDuration = lockDuration;
        this.clock = clock;
    }

    public void checkAllowed(String username) {
        String normalizedUsername = normalize(username);
        AttemptState state = attempts.get(normalizedUsername);
        if (state == null) {
            return;
        }

        Instant now = Instant.now(clock);
        if (state.lockedUntil != null) {
            if (state.lockedUntil.isAfter(now)) {
                throw new TooManyLoginAttemptsException(state.lockedUntil);
            }

            attempts.remove(normalizedUsername);
        }
    }

    public void recordFailure(String username) {
        String normalizedUsername = normalize(username);
        Instant now = Instant.now(clock);
        attempts.compute(normalizedUsername, (key, existing) -> {
            AttemptState state = existing == null ? new AttemptState() : existing;
            state.failedAttempts++;
            if (state.failedAttempts >= maxFailedAttempts) {
                state.lockedUntil = now.plus(lockDuration);
            }
            return state;
        });
    }

    public void recordSuccess(String username) {
        attempts.remove(normalize(username));
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private static final class AttemptState {
        private int failedAttempts;
        private Instant lockedUntil;
    }
}
