package org.in28minutes.springawscognito.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.in28minutes.springawscognito.auth.exception.AuthenticationError;
import org.in28minutes.springawscognito.auth.exception.CIAuthenticationException;
import org.in28minutes.springawscognito.auth.model.User;
import org.in28minutes.springawscognito.auth.util.AwsCognitoJwtParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

/**
 * Created by aayush on 31/10/2018.
 */
@Component
public class CognitoTokenVerifier {

    private static final Logger logger = LoggerFactory.getLogger(CognitoTokenVerifier.class);

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Value("${aws.cognito.pool.id}")
    private String userPoolId;

    // Steps as per: https://docs.amazonaws.cn/en_us/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html

    /**
     *  This method extracts the Bearer token from the HTTPRequest, and then verifies the JWT Token
     *
     * */
    public static User checkRequestAgainstUserPool(HttpServletRequest request, String userPoolId) {

        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null) {
            throw new CIAuthenticationException(401 , "Authorization header not found" , " Cannot proceed with this request");
        }

        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        return checkToken(token, userPoolId, request);
    }

    public static User checkToken(String idToken, String userPoolId, HttpServletRequest request)  {

        // validate JWT token
        AwsCognitoJwtParserUtil.validateJWT(idToken);

        JWTClaimsSet jwtClaimsSet = validateAWSJwtToken(userPoolId , idToken);

        if(!isIdToken(jwtClaimsSet)){
            logger.warn("Token verification failed! , not an ID Token");
            throw new CIAuthenticationException(401, AuthenticationError.TOKEN_TYPE_INCORRECT, " Please check token type");
        }

        logger.trace("Token verification success!");

        User user = new User(jwtClaimsSet.getClaim("cognito:username").toString() ,
                jwtClaimsSet.getClaim("email").toString(), jwtClaimsSet.getClaim("cognito:groups").toString());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, Collections.EMPTY_LIST);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return user;
    }

    /**
     * This validates the Aws Jwt Token using Nimbus Jose Jwt Library. For reference please see.
     * @see <a href= "https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-with-identity-providers.html#amazon-cognito-identity-user-pools-using-id-and-access-tokens-in-web-api"> AWS JWT Token</>
     * @param token
     * @return JWTClaimsSet
     */
    public static JWTClaimsSet validateAWSJwtToken(String userPoolId, String token)  {

        /**
         * AwsCognitoJwtParserUtil class parse the jwt token and gives back the payload.
         */
        try {
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource jwkSource = new ImmutableJWKSet(JWKCache.getJWKSetForuserPoolId(userPoolId, token));

        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;
        JWSKeySelector keySelector = new JWSVerificationKeySelector(jwsAlgorithm, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);

            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return claimsSet;
        } catch (BadJWTException | ParseException | IOException e) {
            logger.error("JWT Token processing failed." , e);
            throw new CIAuthenticationException(401, AuthenticationError.TOKEN_EXPIRED, e.getLocalizedMessage());
        } catch (BadJOSEException | JOSEException e){
            // clear cache to get another public key if it is rotated...
            JWKCache.userPoolIdToJWKPublicKeyMap.clear();
            throw new CIAuthenticationException(401, AuthenticationError.TOKEN_SIGNATURE_ENCRYPTION_ERROR, e.getLocalizedMessage());
        }
    }

    private static boolean isIdToken(JWTClaimsSet claimsSet) {
        return claimsSet.getClaim("token_use").equals("id");
    }
}
