package com.tasktracker.auth;

import com.tasktracker.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String name;
    private Role role;
}
