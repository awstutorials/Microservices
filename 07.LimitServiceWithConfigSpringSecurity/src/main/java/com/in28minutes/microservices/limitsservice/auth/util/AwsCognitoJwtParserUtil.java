package com.in28minutes.microservices.limitsservice.auth.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.in28minutes.microservices.limitsservice.auth.exception.AuthenticationError;
import com.in28minutes.microservices.limitsservice.auth.exception.CIAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Objects;

/**
 * Created by aayush on 31/10/2018.
 */
public class AwsCognitoJwtParserUtil {

    private static final Logger logger = LoggerFactory.getLogger(AwsCognitoJwtParserUtil.class);

    public static final String ISS = "iss";
    public static final String JWK_URl_SUFFIX = "/.well-known/jwks.json";



    private static final int HEADER = 0;
    private static final int PAYLOAD = 1;
    private static final int SIGNATURE = 2;
    private static final int JWT_PARTS = 3;

    /**
     * Returns header for a JWT as a JSON object.
     *
     * @param jwt  Required valid JSON Web Token as String.
     * @return AWS jwt header as a JsonObject.
     */
    public static JsonObject getHeader(String jwt) throws CIAuthenticationException {
        try {
            validateJWT(jwt);
            String header = jwt.split("\\.")[HEADER];
            final byte [] headerBytes = Base64.getUrlDecoder().decode(header);
            final String headerString = new String(headerBytes, "UTF-8");
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(headerString);
            return jsonObject;
        }catch (UnsupportedEncodingException e){
            throw new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, jwt);
        }


    }

    /**
     * Returns payload of a JWT as a JSON object.
     *
     * @param jwt   Required valid JSON Web Token as String.
     * @return AWS jwt payload as a JsonObject.
     */
    public static JsonObject getPayload(String jwt) throws CIAuthenticationException {
        try {
            validateJWT(jwt);
            final String payload = jwt.split("\\.")[PAYLOAD];
            final byte[] payloadBytes =  Base64.getUrlDecoder().decode(payload);
            final String payloadString = new String(payloadBytes, "UTF-8");
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(payloadString);
            return jsonObject;
        } catch ( UnsupportedEncodingException e) {
            throw new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, jwt);
        }
    }

    /**
     * Returns signature of a JWT as a String.
     *
     * @param jwt  Required valid JSON Web Token as String.
     * @return AWS JWT signature as a String.
     */
    public static String getSignature(String jwt) throws CIAuthenticationException {
        try {
            validateJWT(jwt);
            final String signature = jwt.split("\\.")[SIGNATURE];
            final byte[] signatureBytes = Base64.getUrlDecoder().decode(signature);
            return new String(signatureBytes, "UTF-8");
        } catch (final Exception e) {
            throw new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, jwt);
        }
    }

    /**
     * Returns a claim, from the {@code JWT}s' payload, as a String.
     *
     * @param jwt Required valid JSON Web Token as String.
     * @param claim  Required claim name as String.
     * @return  claim from the JWT as a String.
     */
    public static String getClaim(String jwt, String claim) throws CIAuthenticationException {
        try {
            final JsonObject payload = getPayload(jwt);
            final Object claimValue = payload.get(claim);

            if (claimValue != null) {
                return claimValue.toString();
            }

        } catch (final Exception e) {
            throw new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, jwt);
        }
        return null;
    }

    /**
     * Checks if {@code JWT} is a valid JSON Web Token.
     *
     * @param jwt
     */
    public static void validateJWT(String jwt) throws CIAuthenticationException {
        // Check if the the JWT has the three parts
        final String[] jwtParts = jwt.split("\\.");
        if (jwtParts.length != JWT_PARTS) {
            throw new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, jwt);
        }
    }


    /**
     * Parse the Jwt token and get the token issuer URL including user pool id.
     * @param token
     * @return Json Web Key URL
     * @throws CIAuthenticationException
     */
    public static String getJsonWebKeyURL(String userPoolId , String token) throws CIAuthenticationException {

        JsonObject payload = getPayload(token);
        JsonElement issJsonElement = payload.get(ISS);
        if (Objects.isNull(issJsonElement)) {
            throw  new CIAuthenticationException(401, AuthenticationError.NOT_VALID_JSON_WEB_TOKEN, payload.toString());
        }

        String issString = issJsonElement.getAsString();

        String userPoolName = getUserPoolFromPayload(issString);

        if(!userPoolId.equals(userPoolName)){
            throw new CIAuthenticationException(401 , "userpools do not match" ,"We only accept JWTs from allowed userPools");
        }

        String jwkURl = issString + JWK_URl_SUFFIX;

        return jwkURl;
    }

    /**
     * Get the user pool from the iss url.
     * @param issUrl
     * @return ISS - token issuer URL.
     */
    private static String getUserPoolFromPayload(String issUrl) {

        String [] issArray = issUrl.split("amazonaws.com/");
        return issArray[1];
    }
}
