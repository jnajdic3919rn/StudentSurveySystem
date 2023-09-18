package com.example.demo.security;

import com.example.demo.exceptionHandler.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handleException(response, e, ErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        } catch (BadRequestException e) {
            handleException(response, e, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            handleException(response, e, ErrorCode.SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(HttpServletResponse response, Exception e, ErrorCode errorCode, HttpStatus status) throws IOException {
        //ApiErrorResponse apiErrorResponse = new ApiErrorResponse(ZonedDateTime.now(), e.getMessage(), errorCode);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        e.printStackTrace();
        //objectMapper.writeValue(response.getWriter(), apiErrorResponse);
    }
}