package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyDisabledException;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyEnabledException;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyExistsException;
import com.mehrdad.sample.bank.domain.exception.user.UserNotFoundException;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toUserResponse);
    }

    public void enableUser(UUID userId) {
        UserEntity user = loadUserById(userId);
        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new UserAlreadyEnabledException(userId);
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(UUID userId) {
        UserEntity user = loadUserById(userId);
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UserAlreadyDisabledException(userId);
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    private UserEntity loadUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                Boolean.TRUE.equals(user.getEnabled()),
                user.getCreatedAt()
        );
    }
}
