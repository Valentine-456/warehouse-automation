package network.HTTP.authentificators;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import network.HTTP.JWTService;

import java.util.Objects;

public class AuthIsLoggedIn extends Authenticator {
    private final JWTService jwtService = new JWTService();
    @Override
    public Result authenticate(HttpExchange httpExchange) {
        String authorizationHeader = httpExchange.getRequestHeaders().getFirst("Authorization");
        if(authorizationHeader == null)
            return new Failure(403);

        String[] authSplit = authorizationHeader.split(" ");
        if(authSplit.length != 2)
            return new Failure(403);

        if(Objects.equals(authSplit[0], "Bearer")) {
            boolean isJWTCorrect = this.jwtService.verify(authSplit[1]);
            if(isJWTCorrect) {
                return new Success(new HttpPrincipal("user", "realm"));
            }
        }
        return new Failure(403);
    }
}
