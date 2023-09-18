package com.example.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class TokenInfoClient {

  @Bean
  @Qualifier("tokenInfoRestTemplate")
  public RestTemplate tokenInfoRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    ///restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(oauthUri));
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(""));
    return restTemplate;
  }
}
