package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyExistsException;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        String normalizedUsername = request.username().trim();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UserAlreadyExistsException(normalizedUsername);
        }

        UserEntity user = new UserEntity();
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setEnabled(true);

        UserEntity savedUser = userRepository.saveAndFlush(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                Boolean.TRUE.equals(savedUser.getEnabled()),
                savedUser.getCreatedAt()
        );
    }
}
