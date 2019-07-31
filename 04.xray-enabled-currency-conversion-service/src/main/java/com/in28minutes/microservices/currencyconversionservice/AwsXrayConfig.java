package com.in28minutes.microservices.currencyconversionservice;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;

@Configuration
public class AwsXrayConfig {

  @Bean
  public Filter TracingFilter() {
    return new AWSXRayServletFilter("xray-demo-local-conversion-app");
  }
}
