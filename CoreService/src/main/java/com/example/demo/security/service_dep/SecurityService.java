package com.example.demo.security.service_dep;


import com.example.demo.exceptionHandler.UserNotFoundException;
import com.example.demo.model.domain.user.Faculty;
import com.example.demo.model.domain.user.User;
import com.example.demo.model.dto.response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.domain_dep.SecurityUser;
import com.example.demo.security.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        User u = userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("<Security>Employee with lbz <%s> doesn't exist.")
        );
        return new SecurityUser(u);
    }

    public String generateToken(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        String faculty = userRepository.findByUsername(username).get().getFaculty().getShortName();
        return jwtUtils.generateToken(userDetails, faculty);
    }

    public String generateTokenGoogle(UserResponse userResponse) {
        return jwtUtils.generateTokenGoogle(userResponse);
    }
}
