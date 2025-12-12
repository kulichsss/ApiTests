package tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private static final String URL_AUTH = "https://playground.learnqa.ru/api/user/";
    private static final String URL_LOGIN = "https://playground.learnqa.ru/api/user/login";

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Negative case. Deleting a user by ID 2")
    @Description("Attempt to delete a user under an authorized system user. User ID 2")
    public void deleteSystemUserData() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(URL_LOGIN, userData);
        String userId = responseGetAuth.jsonPath().getString("user_id");

        //Edit with wrong id
        String newName = "C";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseDeleteUser = apiCoreRequests.makeDeletedRequestWithHeaderAndCookie(
                URL_AUTH + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @DisplayName("Positive case. Delete new user")
    @Description("Create a user, log in as the user, delete the user, " +
            "then try to retrieve the user's data using the ID and verify that the user has been deleted.")
    public void deleteCommonUserData() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        Map<String, String> userDataWithEmail = DataGenerator.getRegistrationData(userData);

        //AUTH
        JsonPath responseCreateAuth = apiCoreRequests.makeJsonPostRequest(URL_AUTH, userDataWithEmail);
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userDataWithEmail.get("email"));
        authData.put("password", userDataWithEmail.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(URL_LOGIN, authData);

        //DELETED
        Response responseDeleteUser = apiCoreRequests.makeDeletedRequestWithHeaderAndCookie(
                URL_AUTH + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        //CHECK
        Response responseGetAuthCheck = apiCoreRequests.makePostRequest(URL_LOGIN, authData);
        Assertions.assertResponseTextEquals(responseGetAuthCheck, "Invalid username/password supplied");
    }

    @Test
    @DisplayName("Negative case. Deleting a user by another user")
    @Description("Try to delete a user while logged in as another user")
    public void deleteUserDataWithAnotherUser() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        Map<String, String> userDataWithEmail = DataGenerator.getRegistrationData(userData);

        //AUTH
        JsonPath responseCreateAuth = apiCoreRequests.makeJsonPostRequest(URL_AUTH, userDataWithEmail);
        String userId = responseCreateAuth.getString("id");
        String anotherUserId = "12345";

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userDataWithEmail.get("email"));
        authData.put("password", userDataWithEmail.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(URL_LOGIN, authData);

        //DELETED
        Response responseDeleteUser = apiCoreRequests.makeDeletedRequestWithHeaderAndCookie(
                URL_AUTH + (userId.equals(anotherUserId)?anotherUserId+"1":anotherUserId),
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"This user can only delete their own account.\"}");
    }
}
