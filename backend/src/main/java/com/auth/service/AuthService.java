package com.auth.service;

import com.auth.dto.*;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ApiResponse register(RegisterRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return new ApiResponse(false, "Username is required");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return new ApiResponse(false, "Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return new ApiResponse(false, "Password is required");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new ApiResponse(false, "Passwords do not match");
        }
        if (request.getPassword().length() < 6) {
            return new ApiResponse(false, "Password must be at least 6 characters");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        return new ApiResponse(true, "User registered successfully", 
            new UserProfileResponse(savedUser.getId(), savedUser.getUsername(), 
                                    savedUser.getEmail(), savedUser.getRole(), savedUser.getIsActive()));
    }

    public ApiResponse login(LoginRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return new ApiResponse(false, "Username or email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return new ApiResponse(false, "Password is required");
        }

        // Find user by username or email
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(request.getUsername());
        }

        if (userOptional.isEmpty()) {
            return new ApiResponse(false, "Invalid username/email or password");
        }

        User user = userOptional.get();

        // Check if account is active
        if (!user.getIsActive()) {
            return new ApiResponse(false, "Account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ApiResponse(false, "Invalid username/email or password");
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());

        return new ApiResponse(true, "Login successful", response);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

}
