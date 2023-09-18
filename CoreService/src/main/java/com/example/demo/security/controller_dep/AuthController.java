package com.example.demo.security.controller_dep;


import com.example.demo.model.constants.Role;
import com.example.demo.model.domain.user.User;
import com.example.demo.model.dto.response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.dto_dep.TokenRequest;
import com.example.demo.security.dto_dep.TokenRequestGoogle;
import com.example.demo.security.dto_dep.TokenResponse;
import com.example.demo.security.service_dep.SecurityService;
import com.example.demo.service.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityService securityService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/try")
    public String publicMethod(){
        /**
        User user = new User();
        user.setUsername("mijanajdic@raf.rs");
        user.setPassword(passwordEncoder.encode("mijanajdic"));
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);
         */
        return "Jej, public";
    }

    @PostMapping
    @RequestMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody @NotNull TokenRequest tokenRequest) {
        authenticate(tokenRequest.username, tokenRequest.password);
        String token = securityService.generateToken(tokenRequest.getUsername());
        return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
    }

    private void authenticate(@NotNull @NotBlank String username, @NotNull @NotBlank String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("Your account has been disabled", e);
        } catch (Exception e) {
            throw new BadCredentialsException("Wrong username or password", e);
        }
    }

    @PostMapping
    @RequestMapping("/google")
    public ResponseEntity<TokenResponse> authenticateWithGoogle(@RequestBody TokenRequestGoogle tokenRequestGoogle) {
        boolean ret = tokenService.validateToken(tokenRequestGoogle.getAccessToken().substring(7));
        if(ret == true) {
            UserResponse userResponse = tokenService.getRole(tokenRequestGoogle.getAccessToken().substring(7));
            if(userResponse != null) {
                String token = securityService.generateTokenGoogle(userResponse);
                return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(new TokenResponse("Nemate pristup"), HttpStatus.BAD_REQUEST);

    }



}

