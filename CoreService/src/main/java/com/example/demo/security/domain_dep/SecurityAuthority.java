package com.example.demo.security.domain_dep;


import com.example.demo.model.constants.Role;
import org.springframework.security.core.GrantedAuthority;

public class SecurityAuthority implements GrantedAuthority {
    private final Role role;

    public SecurityAuthority(Role role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.name();
    }
}
