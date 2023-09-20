package com.example.demo.model.dto.response;

import com.example.demo.model.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Role role;
    private String username;
    private String faculty;
}
