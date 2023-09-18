package com.example.demo.security;

import com.example.demo.service.TokenService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;

@Aspect
@Configuration
public class AutAspect { ;

  private TokenService tokenService;

  public AutAspect(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Around("@annotation(com.example.demo.security.AutCheck)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    //Get method signature
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    //Check for authorization parameter
    String token = null;
    for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
      if (methodSignature.getParameterNames()[i].equals("authorization")) {
        //Check bearer schema
        if (joinPoint.getArgs()[i].toString().startsWith("Bearer")) {
          //Get token
          token = joinPoint.getArgs()[i].toString().split(" ")[1];
        }
      }
    }
    //If token is not presents return UNAUTHORIZED response
    if (token == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    //Validate token
    boolean ans = tokenService.validateToken(token);

    //If fails return UNAUTHORIZED response
    if(!ans)
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    else
      return joinPoint.proceed();
  }

}
