package com.in28minutes.microservices.limitsservice.auth;

import com.in28minutes.microservices.limitsservice.auth.exception.CIAuthenticationException;
import com.in28minutes.microservices.limitsservice.auth.util.AwsCognitoJwtParserUtil;
import com.nimbusds.jose.jwk.JWKSet;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JWKCache {

    public static final Map<String, JWKSet> userPoolIdToJWKPublicKeyMap = new ConcurrentHashMap<>();

    public static JWKSet getJWKSetForuserPoolId(String userPoolId , String token) throws CIAuthenticationException, IOException, ParseException {

        // first check from cache
        JWKSet jwkSet = userPoolIdToJWKPublicKeyMap.get(userPoolId);

        if(jwkSet == null){
            // get from remote URL
            JWKSet load = JWKSet.load(new URL(AwsCognitoJwtParserUtil.getJsonWebKeyURL(userPoolId , token)));
            userPoolIdToJWKPublicKeyMap.put(userPoolId , load);
        }

        return userPoolIdToJWKPublicKeyMap.get(userPoolId);
    }
}
