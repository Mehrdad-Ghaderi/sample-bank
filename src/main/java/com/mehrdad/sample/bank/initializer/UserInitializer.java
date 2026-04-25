package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.bootstrap-user.enabled:false}")
    private boolean bootstrapEnabled;

    @Value("${app.security.bootstrap-user.username:}")
    private String username;

    @Value("${app.security.bootstrap-user.password:}")
    private String password;

    @Value("${app.security.bootstrap-user.role:USER}")
    private UserRole role;

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Bootstrap user username and password must be configured");
        }

        if (userRepository.existsByUsername(username)) {
            return;
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);

        userRepository.save(user);
    }
}
