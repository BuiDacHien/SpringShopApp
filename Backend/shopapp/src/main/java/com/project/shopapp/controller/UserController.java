package com.project.shopapp.controller;

import com.project.shopapp.dto.*;
import com.project.shopapp.entity.User;
import com.project.shopapp.response.LoginResponse;
import com.project.shopapp.response.RegisterResponse;
import com.project.shopapp.response.UserResponse;
import com.project.shopapp.service.UserService;
import com.project.shopapp.utils.CommonStrings;
import com.project.shopapp.components.LocalizationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.base.path}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody UserDto userDto, BindingResult result) {
        try {
            RegisterResponse registerResponse = new RegisterResponse();

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage)
                        .toList();
                registerResponse.setMessage(errorMessages.toString());
                return ResponseEntity.badRequest().body(registerResponse);
            }

            if (!userDto.getPassword().equals(userDto.getRetypePassword())) {
                registerResponse.setMessage(localizationUtils.getLocalizedMessage(CommonStrings.RETYPE_PASSWORD_NOT_MATCH));
                return ResponseEntity.badRequest().body(registerResponse);
            }

            User newUser = userService.createUser(userDto);
            registerResponse.setMessage(localizationUtils.getLocalizedMessage(CommonStrings.REGISTER_SUCCESSFULLY));
            registerResponse.setUser(newUser);
            return ResponseEntity.ok(registerResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(CommonStrings.REGISTER_FAILED, e.getMessage()))
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(LoginResponse.builder().message(errorMessages.toString()).build());
            }


            String token = userService.loginUser(
                    userLoginDto.getPhoneNumber(),
                    userLoginDto.getPassword(),
                    userLoginDto.getRoleId() == null ? 1 : userLoginDto.getRoleId());
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(CommonStrings.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(CommonStrings.LOGIN_FAILED, e.getMessage()))
                    .build());
        }
    }

    @GetMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.mapFromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDto updatedUserDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            // Ensure that the user making the request matches the user being updated
            if (!Objects.equals(user.getId(), userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updatedUserDto);
            return ResponseEntity.ok(UserResponse.mapFromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
