package com.project.shopapp.service.Impl;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.dto.UserDto;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.DataAlreadyExistException;
import com.project.shopapp.exception.InValidParamException;
import com.project.shopapp.exception.PermissionDenyException;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDto userDto) throws PermissionDenyException {

        String phoneNumber = userDto.getPhoneNumber();
        String email = userDto.getEmail();

        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataAlreadyExistException("Phone number or Email already exists");
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userDto.getRoleId()));

        if (role.getName().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You can't register an admin account");
        }
        User newUser = User.builder()
                .fullName(userDto.getFullName())
                .phoneNumber(userDto.getPhoneNumber())
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .password(userDto.getPassword())
                .dateOfBirth(userDto.getDateOfBirth())
                .facebookAccountId(userDto.getFacebookAccountId())
                .googleAccountId(userDto.getGoogleAccountId())
                .isActive(true).build();

        newUser.setRole(role);

        if (userDto.getFacebookAccountId() == 0 || userDto.getGoogleAccountId() == 0) {
            String password = userDto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);

        }

        return userRepository.save(newUser);
    }

    @Override
    public String loginUser(String phoneNumber, String password) {

        Optional<User> loginUser = userRepository.findByPhoneNumber(phoneNumber);
        if (loginUser.isEmpty()) {
            throw new ResourceNotFoundException("Invalid phone number/email or password");
        }

        User existingUser = loginUser.get();

        if (existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, existingUser.getAuthorities()
        );


        authenticationManager.authenticate(authenticationToken);


        try {
            return jwtTokenUtil.generateToken(loginUser.get());
        } catch (InValidParamException e) {
            throw new RuntimeException(e);
        }
    }
}
