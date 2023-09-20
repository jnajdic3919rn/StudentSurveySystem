package com.example.demo.unit;

import com.example.demo.model.constants.Role;
import com.example.demo.model.dto.response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service_dep.SecurityService;
import com.example.demo.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private SecurityService securityService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        tokenService = new TokenService("oauthUri", "oauthUserInfo", restTemplate, userRepository);
    }

    @Test
    void testValidateTokenForInvalidToken() {
        String invalidToken = "invalidToken";
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(responseEntity);

        boolean result = tokenService.validateToken(invalidToken);

        assertFalse(result);
    }

    @Test
    void testValidateTokenForValidToken() {
        String validToken = securityService.generateToken("jnajdic@raf.rs");
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(responseEntity);

        boolean result = tokenService.validateToken(validToken);

        assertTrue(result);
    }

    @Test
    void testGetRoleForValidToken() {
        String token = securityService.generateToken("jnajdic@raf.rs");
        ResponseEntity<Object> responseEntity = new ResponseEntity<>("email=jnajdic@raf.rs, hd=faculty", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(responseEntity);

        UserResponse userResponse = tokenService.getRole(token);

        assertNotNull(userResponse);
        assertEquals(Role.ROLE_PROFESOR, userResponse.getRole());
    }

}
