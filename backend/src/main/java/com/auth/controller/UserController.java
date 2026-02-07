package com.auth.controller;

import com.auth.dto.UserProfileResponse;
import com.auth.dto.ApiResponse;
import com.auth.model.User;
import com.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized"));
        }

        String username = authentication.getName();
        Optional<User> userOptional = authService.getUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive()
            );
            return ResponseEntity.ok(new ApiResponse(true, "User retrieved successfully", profile));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse(false, "User not found"));
        }
    }

}
