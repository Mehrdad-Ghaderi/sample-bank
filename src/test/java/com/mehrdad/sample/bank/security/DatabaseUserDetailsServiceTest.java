package com.mehrdad.sample.bank.security;

import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameShouldReturnSpringUserDetails() {
        UserEntity user = user("user", "$2a$10$hash", UserRole.USER, true);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("user");

        assertEquals("user", result.getUsername());
        assertEquals("$2a$10$hash", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByUsername("user");
    }

    @Test
    void loadUserByUsernameShouldMapDisabledUser() {
        UserEntity user = user("admin", "$2a$10$hash", UserRole.ADMIN, false);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertFalse(result.isEnabled());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository).findByUsername("admin");
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing")
        );

        verify(userRepository).findByUsername("missing");
    }

    private static UserEntity user(String username, String passwordHash, UserRole role, boolean enabled) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setEnabled(enabled);
        return user;
    }
}
