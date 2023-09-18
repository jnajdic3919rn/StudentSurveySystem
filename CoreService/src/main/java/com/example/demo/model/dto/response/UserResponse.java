package com.example.demo.model.dto.response;

import com.example.demo.model.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponse {
    private Role role;
    private String username;
    private String faculty;
}
