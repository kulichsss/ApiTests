package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
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

public class UserEditTest extends BaseTestCase {
    private static final String URL_AUTH = "https://playground.learnqa.ru/api/user/";
    private static final String URL_LOGIN = "https://playground.learnqa.ru/api/user/login";

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Negative case. Edit user data while being unauthorized")
    @Description("Let's try to change the user's data while being unauthorized")
    public void editUserDataWithUnauth() {

        //Edit with wrong id
        String newName = "Change Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                    URL_AUTH + "11",
                    editData
            );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    @DisplayName("Negative case. Edit user data while being authorized by another user")
    @Description("Let's try to change the user's data while being authorized by another user")
    public void editUserDataWithAnotherUser() {
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

        //Edit with wrong id
        String newName = "Change Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                URL_AUTH + "11",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"This user can only edit their own data.\"}");
    }

    @Test
    @DisplayName("Negative case. Edit user's email on incorrect")
    @Description("Let's try to change the user's email while being logged in as the same user, to a new email without the @ symbol")
    public void editUserDataWithWrongEmail() {
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

        //Edit with wrong id
        String newEmail = DataGenerator.getWrongEmail(5);
        Map<String,String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                URL_AUTH + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Invalid email format\"}");
    }

    @Test
    @DisplayName("Negative case. Edit user's firstname on incorrect")
    @Description("Let's try to change the user's firstName while being logged in as the same user, to a very short value of one character")
    public void editUserDataWithTooShortFirstname() {
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

        //Edit with wrong id
        String newName = "C";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                URL_AUTH + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"The value for field `firstName` is too short\"}");
    }
}
