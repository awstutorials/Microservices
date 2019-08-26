package com.in28minutes.microservices.limitsservice.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public class User {

    private final String username;
    private final String email;
    private final List<String> groups;


    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("email") String email, final String group) {
        this.username = username;
        this.email = email;

        List<String> authorities = new ArrayList<>();
        authorities.add(group);
        this.groups = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + this.getUsername() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", groups='" + groups + '\'' +
                '}';
    }
}
