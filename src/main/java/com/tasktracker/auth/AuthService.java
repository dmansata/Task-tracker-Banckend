package com.tasktracker.auth;

import com.tasktracker.exception.ForbiddenException;
import com.tasktracker.security.JwtService;
import com.tasktracker.security.TokenBlacklistService;
import com.tasktracker.user.Role;
import com.tasktracker.user.User;
import com.tasktracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ForbiddenException("Email is already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getName(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public void logout(String token) {
        tokenBlacklistService.blacklist(token, jwtService.extractExpiration(token));
    }
}
