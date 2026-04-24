package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyExistsException;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldPersistEncodedPasswordAndReturnResponse() {
        CreateUserRequest request = new CreateUserRequest("  alice  ", "password123", UserRole.ADMIN);
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-23T12:00:00Z");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            savedUser.setId(userId);
            savedUser.setCreatedAt(createdAt);
            return savedUser;
        });

        UserResponse result = userService.createUser(request);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();

        assertEquals("alice", savedUser.getUsername());
        assertEquals("encoded-password", savedUser.getPasswordHash());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals(Boolean.TRUE, savedUser.getEnabled());

        assertEquals(userId, result.id());
        assertEquals("alice", result.username());
        assertEquals(UserRole.ADMIN, result.role());
        assertEquals(true, result.enabled());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    void createUserShouldRejectDuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest("alice", "password123", UserRole.USER);

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));

        verify(userRepository).existsByUsername("alice");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).saveAndFlush(any());
    }
}
