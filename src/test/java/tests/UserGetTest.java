package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorisation cases")
@Feature("Authorization")
public class UserGetTest extends BaseTestCase {

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Issue("BUG-789")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Requesting another user's data")
    @Description("Log in as one user, but get user's data from another")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authUser = new HashMap<>();
        authUser.put("email", "vinkotov@example.com");
        authUser.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(getUrlLogin(), authUser);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequestWithHeaderAndCookie(
                "https://playground.learnqa.ru/api/user/3",
                header,
                cookie
        );

        Assertions.assertJsonHasKey(responseUserData, "username");
        Assertions.assertJsonNotHasKey(responseUserData, "firstName");
        Assertions.assertJsonNotHasKey(responseUserData, "lastName");
        Assertions.assertJsonNotHasKey(responseUserData, "email");
    }
}
