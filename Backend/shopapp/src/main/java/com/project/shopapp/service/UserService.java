package com.project.shopapp.service;

import com.project.shopapp.dto.UpdateUserDto;
import com.project.shopapp.dto.UserDto;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.PermissionDenyException;

public interface UserService {
    User createUser(UserDto userDto) throws PermissionDenyException;

    String loginUser(String phoneNumber, String password, Long roleId);

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDto updatedUserDto) throws Exception;
}
