package com.project.shopapp.service.Impl;

import com.project.shopapp.components.JwtTokenUtil;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dto.UpdateUserDto;
import com.project.shopapp.dto.UserDto;
import com.project.shopapp.entity.Role;
import com.project.shopapp.entity.Token;
import com.project.shopapp.entity.User;
import com.project.shopapp.exception.DataAlreadyExistException;
import com.project.shopapp.exception.InValidParamException;
import com.project.shopapp.exception.PermissionDenyException;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.TokenRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.service.UserService;
import com.project.shopapp.utils.CommonStrings;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    private final LocalizationUtils localizationUtils;

    private final TokenRepository tokenRepository;


    @Override
    @Transactional
    public User createUser(UserDto userDto) throws PermissionDenyException {

        String phoneNumber = userDto.getPhoneNumber();
        String email = userDto.getEmail();

        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataAlreadyExistException(localizationUtils.getLocalizedMessage(CommonStrings.PHONE_NUMBER_OR_EMAIL_ALREADY_EXIST));
        }

        Role role = roleRepository.findById(userDto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException(localizationUtils.getLocalizedMessage(CommonStrings.ROLE_DOES_NOT_EXISTS)));

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
    public String loginUser(String phoneNumber, String password, Long roleId) {

        Optional<User> loginUser = userRepository.findByPhoneNumber(phoneNumber);
        if (loginUser.isEmpty()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizedMessage(CommonStrings.WRONG_PHONE_OR_PASSWORD));
        }

        User existingUser = loginUser.get();

        if (existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(CommonStrings.WRONG_PHONE_OR_PASSWORD));
            }
        }

        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizedMessage(CommonStrings.ROLE_DOES_NOT_EXISTS));
        }
        if(!loginUser.get().getIsActive()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizedMessage(CommonStrings.USER_IS_LOCKED));
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

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDto updatedUserDto) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDto.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
                userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user information based on the DTO
        if (updatedUserDto.getFullName() != null) {
            existingUser.setFullName(updatedUserDto.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDto.getAddress() != null) {
            existingUser.setAddress(updatedUserDto.getAddress());
        }
        if (updatedUserDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDto.getDateOfBirth());
        }
        if (updatedUserDto.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updatedUserDto.getFacebookAccountId());
        }
        if (updatedUserDto.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDto.getGoogleAccountId());
        }

        // Update the password if it is provided in the Dto
        if (updatedUserDto.getPassword() != null
                && !updatedUserDto.getPassword().isEmpty()) {
            if(!updatedUserDto.getPassword().equals(updatedUserDto.getRetypePassword())) {
                throw new ResourceNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDto.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        // existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }
}
