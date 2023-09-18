package com.example.demo.security.dto_dep;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    public String username;
    public String password;
}
