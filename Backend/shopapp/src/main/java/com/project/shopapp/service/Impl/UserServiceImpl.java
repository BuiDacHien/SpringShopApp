package com.project.shopapp.service.Impl;

import com.project.shopapp.dto.UserDto;
import com.project.shopapp.dto.UserLoginDto;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.DataAlreadyExist;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(UserDto userDto) {

        String phoneNumber = userDto.getPhoneNumber();
        String email = userDto.getEmail();

        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataAlreadyExist("Phone number or Email already exists");
        }

        User newUser = User.builder()
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .password(userDto.getPassword())
                .dateOfBirth((Date) userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .isActive(true).build();

        Role role = roleRepository.findById(userDto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userDto.getRoleId()));
        newUser.setRole(role);

        if (userDto.getFacebookAccountId() == 0 || userDto.getGoogleAccountId() == 0) {
            String password = userDto.getPassword();
        }

        return userRepository.save(newUser);
    }

    @Override
    public String loginUser(UserLoginDto userLoginDto) {
        String phoneNumberOrEmail = userLoginDto.getPhoneNumberOrEmail();
        String password = userLoginDto.getPassword();

        Optional<User> loginUser = userRepository.findByPhoneNumberOrEmail(phoneNumberOrEmail);
        if (loginUser.isPresent()) {
            User user = loginUser.get();
            if (user.getPassword().equals(password)) {
                return "Login successfully!";
            }
        }
        return "Invalid phone number/email or password";
    }
}
