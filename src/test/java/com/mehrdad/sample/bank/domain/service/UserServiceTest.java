package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyDisabledException;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyEnabledException;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyExistsException;
import com.mehrdad.sample.bank.domain.exception.user.UserNotFoundException;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void getUsersShouldReturnPagedUserResponses() {
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-24T10:00:00Z");
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("alice");
        user.setRole(UserRole.ADMIN);
        user.setEnabled(true);
        user.setCreatedAt(createdAt);

        when(userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 5), 1));

        Page<UserResponse> result = userService.getUsers(PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
        assertEquals("alice", result.getContent().getFirst().username());
        assertEquals(UserRole.ADMIN, result.getContent().getFirst().role());
        assertEquals(true, result.getContent().getFirst().enabled());
        assertEquals(createdAt, result.getContent().getFirst().createdAt());
    }

    @Test
    void enableUserShouldSetEnabledTrue() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEnabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.enableUser(userId);

        assertEquals(Boolean.TRUE, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void enableUserShouldRejectAlreadyEnabledUser() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEnabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyEnabledException.class, () -> userService.enableUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void disableUserShouldSetEnabledFalse() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEnabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.disableUser(userId);

        assertEquals(Boolean.FALSE, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void disableUserShouldRejectAlreadyDisabledUser() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEnabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyDisabledException.class, () -> userService.disableUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void enableUserShouldRejectMissingUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.enableUser(userId));
    }
}
