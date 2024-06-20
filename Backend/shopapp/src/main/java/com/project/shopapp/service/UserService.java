package com.project.shopapp.service;

import com.project.shopapp.dto.UserDto;
import com.project.shopapp.dto.UserLoginDto;
import com.project.shopapp.entity.User;

public interface UserService {
    User createUser(UserDto userDto);

    String loginUser(UserLoginDto userLoginDto);
}
