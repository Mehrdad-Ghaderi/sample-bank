package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.user.ChangePasswordRequest;
import com.mehrdad.sample.bank.api.dto.PageResponse;
import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.ResetPasswordRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_BASE_PATH + ApiPaths.USERS)
public class UserController {

    private static final String USER_RESOURCE_PATH = "/{userId}";
    private static final String USER_ENABLE_PATH = USER_RESOURCE_PATH + "/enable";
    private static final String USER_DISABLE_PATH = USER_RESOURCE_PATH + "/disable";
    private static final String USER_PASSWORD_RESET_PATH = USER_RESOURCE_PATH + "/password/reset";
    private static final String MY_PASSWORD_PATH = "/me/password";

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.createFrom(userService.getUsers(pageable)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(ApiPaths.USER_RESOURCE)
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PatchMapping(USER_ENABLE_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enableUser(@PathVariable UUID userId) {
        userService.enableUser(userId);
    }

    @PatchMapping(USER_DISABLE_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableUser(@PathVariable UUID userId) {
        userService.disableUser(userId);
    }

    @PatchMapping(USER_PASSWORD_RESET_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@PathVariable UUID userId, @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(userId, request);
    }

    @PatchMapping(MY_PASSWORD_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
    }
}
