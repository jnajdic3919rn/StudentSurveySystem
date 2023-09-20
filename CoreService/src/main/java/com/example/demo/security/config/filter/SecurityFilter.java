package com.example.demo.security.config.filter;


import com.example.demo.exceptionHandler.exceptions.jwt.CantParseJwtException;
import com.example.demo.security.util.JwtUtils;
import com.example.demo.util.JsonBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // for password reset
            String tokenHeader = extractJwtToken(request.getRequestURI());
            boolean param = true;

            if(tokenHeader == null || tokenHeader.equals("")){
                tokenHeader = request.getHeader("Authorization");
                param = false;
            }

            String email = null;
            String jwtToken = null;

            if (tokenHeader != null && (tokenHeader.startsWith("Bearer ") || param)) {
                if(param){
                    jwtToken = tokenHeader;
                }else{
                    jwtToken = tokenHeader.substring(7);
                }
                email = jwtUtils.getUsernameFromToken(jwtToken);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtils.validateToken(jwtToken)) {

                    List<GrantedAuthority> authorities = jwtUtils.getAuthoritiesFromToken(jwtToken);

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            email, null, authorities
                    );

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
            filterChain.doFilter(request, response);
        } catch (CantParseJwtException | ExpiredJwtException | MalformedJwtException e) {
            handleJwtException(response, e);
        }
    }

    private void handleJwtException(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        String error = "Unknown error.";

        if (ex instanceof CantParseJwtException || ex instanceof ExpiredJwtException || ex instanceof MalformedJwtException) {
            error = "Error parsing jwt. Double check the jwt you are sending.";
        }

        Map<String, Object> json = new JsonBuilder()
                .put("timestamp", Date.from(Instant.now()).toString())
                .put("error", error)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(json);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private String extractJwtToken(String requestUri) {
        String[] uriParts = requestUri.split("/");
        String jwtToken = null;

        for (int i = 0; i < uriParts.length; i++) {
            if (uriParts[i].equals("password-reset") && i < uriParts.length - 2 && uriParts.length == i + 4) {
                jwtToken = uriParts[i + 3];
                break;
            }
        }

        return jwtToken;
    }

}
