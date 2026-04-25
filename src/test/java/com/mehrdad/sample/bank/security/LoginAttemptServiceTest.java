package com.mehrdad.sample.bank.security;

import com.mehrdad.sample.bank.security.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginAttemptServiceTest {

    @Test
    void checkAllowedShouldBlockAfterThreshold() {
        MutableClock clock = new MutableClock(Instant.parse("2026-04-25T00:00:00Z"));
        LoginAttemptService service = new LoginAttemptService(3, Duration.ofMinutes(15), clock);

        service.recordFailure("user");
        service.recordFailure("user");
        service.recordFailure("user");

        assertThrows(TooManyLoginAttemptsException.class, () -> service.checkAllowed("user"));
    }

    @Test
    void recordSuccessShouldClearFailures() {
        MutableClock clock = new MutableClock(Instant.parse("2026-04-25T00:00:00Z"));
        LoginAttemptService service = new LoginAttemptService(3, Duration.ofMinutes(15), clock);

        service.recordFailure("user");
        service.recordFailure("user");
        service.recordSuccess("user");

        assertDoesNotThrow(() -> service.checkAllowed("user"));
    }

    @Test
    void checkAllowedShouldPermitAfterLockExpires() {
        MutableClock clock = new MutableClock(Instant.parse("2026-04-25T00:00:00Z"));
        LoginAttemptService service = new LoginAttemptService(3, Duration.ofMinutes(15), clock);

        service.recordFailure("user");
        service.recordFailure("user");
        service.recordFailure("user");

        clock.setInstant(Instant.parse("2026-04-25T00:16:00Z"));

        assertDoesNotThrow(() -> service.checkAllowed("user"));
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void setInstant(Instant instant) {
            this.instant = instant;
        }
    }
}
