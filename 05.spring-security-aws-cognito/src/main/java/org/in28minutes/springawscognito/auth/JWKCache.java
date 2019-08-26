package org.in28minutes.springawscognito.auth;

import com.nimbusds.jose.jwk.JWKSet;
import org.in28minutes.springawscognito.auth.exception.CIAuthenticationException;
import org.in28minutes.springawscognito.auth.util.AwsCognitoJwtParserUtil;

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
