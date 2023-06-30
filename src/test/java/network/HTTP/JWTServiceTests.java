package network.HTTP;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JWTServiceTests {

    private JWTService jwtService;

    @Before
    public void setup() {
        jwtService = new JWTService();
    }

    @Test
    public void testSignMethodReturnsNonNullToken() {
        String token = jwtService.sign("testUser");
        Assert.assertNotNull(token);
    }

    @Test
    public void testSignMethodReturnsStringToken() {
        String token = jwtService.sign("testUser");
        Assert.assertTrue(token instanceof String);
    }

    @Test
    public void testJWTServiceVerifiesItsOwnToken() {
        String userLogin = "testUser";
        String token = jwtService.sign(userLogin);
        boolean isCorrect = jwtService.verify(token);
        Assert.assertTrue(isCorrect);
    }
}
