package network.HTTP;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.UUID;

public class JWTService {
    private final String issuer = "StoreServer";
    private final String secret = "This-is_a-JWT-Key-for-client-server-app";
    private Algorithm algorithm = Algorithm.HMAC256(secret);
    private JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build();

    public String sign(String userLogin) {
        String jwtToken = JWT.create()
                .withIssuer(issuer)
                .withClaim("login", userLogin)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000L))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
        return jwtToken;
    }

    public boolean verify(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
