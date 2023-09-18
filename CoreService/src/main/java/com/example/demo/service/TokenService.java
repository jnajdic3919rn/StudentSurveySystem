package com.example.demo.service;

import com.example.demo.model.constants.Role;
import com.example.demo.model.dto.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class TokenService {

  //@Value("${google.oauth.uri}")
  private String oauthUri;

  //@Value("${google.oauth.userInfo.uri}")
  private String oauthUserInfo;

  //@Qualifier("tokenInfoRestTemplate")
  private RestTemplate restTemplate;

  public TokenService(@Value("${google.oauth.uri}") String oauthUri,
                      @Value("${google.oauth.userInfo.uri}") String oauthUserInfo,
                      @Qualifier("tokenInfoRestTemplate") RestTemplate restTemplate){

    this.oauthUri = oauthUri;
    this.oauthUserInfo = oauthUserInfo;
    this.restTemplate = restTemplate;

  }

  public boolean validateToken(String token) {
    System.out.println(token);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(token);
    HttpEntity httpEntity = new HttpEntity<>(null, httpHeaders);
    try {
      ResponseEntity<Object> responseEntity = restTemplate.exchange(oauthUri, HttpMethod.GET, httpEntity, Object.class);
      int statusCode = responseEntity.getStatusCodeValue();

      if (statusCode == 200)
        return true;
      else
        return false;

    } catch (Exception e) {
      return false;
    }
  }

    public UserResponse getRole(String token){
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      HttpEntity httpEntity = new HttpEntity<>(null, httpHeaders);
      try {
        ResponseEntity<Object> responseEntity = restTemplate.exchange(oauthUserInfo, HttpMethod.GET, httpEntity, Object.class);
        int statusCode = responseEntity.getStatusCodeValue();

        if (statusCode == 200) {
          return getUserInfo(responseEntity.getBody().toString());
        }
          else {
          throw new RuntimeException();
        }
      }
      catch (Exception e){
        e.printStackTrace();
      }

      return null;

  }

  private UserResponse getUserInfo(String input){
    String[] keyValuePairs = input.split(", ");

    // Initialize email as null
    String email = null;
    String faculty = null;

    // Iterate through the key-value pairs to find the email
    for (String pair : keyValuePairs) {
      String[] keyValue = pair.split("=");
      if (keyValue.length == 2 && keyValue[0].equals("email")) {
        email = keyValue[1];
      }
      if (keyValue.length == 2 && keyValue[0].equals("hd")){
        faculty = keyValue[1].split("[.]")[0].toUpperCase(Locale.ROOT);
      }

      if(faculty != null && email != null) break;
    }

    UserResponse userResponse;
    if (email != null && faculty != null) {
      if(containsNumbers(email))
        userResponse = new UserResponse(Role.ROLE_STUDENT, email, faculty);
      else
        userResponse = new UserResponse(Role.ROLE_PROFESOR, email, faculty);
    } else {
      throw new RuntimeException();
    }
    return userResponse;
  }

  private static boolean containsNumbers(String email) {
    return Pattern.compile("\\d").matcher(email).find();
  }
}
