package tests;

import io.qameta.allure.*;
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

@Epic("Authorisation cases")
@Feature("Authorization")
public class UserEditTest extends BaseTestCase {

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Issue("BUG-781")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative case. Edit user data while being unauthorized")
    @Description("Let's try to change the user's data while being unauthorized")
    public void editUserDataWithUnauth() {

        //Edit with wrong id
        String newName = "Change Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                    getUrlAuth() + "11",
                    editData
            );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    @Issue("BUG-780")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative case. Edit user data while being authorized by another user")
    @Description("Let's try to change the user's data while being authorized by another user")
    public void editUserDataWithAnotherUser() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        Map<String, String> userDataWithEmail = DataGenerator.getRegistrationData(userData);

        //AUTH
        JsonPath responseCreateAuth = apiCoreRequests.makeJsonPostRequest(getUrlAuth(), userDataWithEmail);
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userDataWithEmail.get("email"));
        authData.put("password", userDataWithEmail.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(getUrlLogin(), authData);

        //Edit with wrong id
        String newName = "Change Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                getUrlAuth() + "11",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"This user can only edit their own data.\"}");
    }

    @Test
    @Issue("BUG-782")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative case. Edit user's email on incorrect")
    @Description("Let's try to change the user's email while being logged in as the same user, to a new email without the @ symbol")
    public void editUserDataWithWrongEmail() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        Map<String, String> userDataWithEmail = DataGenerator.getRegistrationData(userData);

        //AUTH
        JsonPath responseCreateAuth = apiCoreRequests.makeJsonPostRequest(getUrlAuth(), userDataWithEmail);
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userDataWithEmail.get("email"));
        authData.put("password", userDataWithEmail.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(getUrlLogin(), authData);

        //Edit with wrong id
        String newEmail = DataGenerator.getWrongEmail(5);
        Map<String,String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                getUrlAuth() + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Invalid email format\"}");
    }

    @Test
    @Issue("BUG-783")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Negative case. Edit user's firstname on incorrect")
    @Description("Let's try to change the user's firstName while being logged in as the same user, to a very short value of one character")
    public void editUserDataWithTooShortFirstname() {
        String email  = DataGenerator.getRandomEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        Map<String, String> userDataWithEmail = DataGenerator.getRegistrationData(userData);

        //AUTH
        JsonPath responseCreateAuth = apiCoreRequests.makeJsonPostRequest(getUrlAuth(), userDataWithEmail);
        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userDataWithEmail.get("email"));
        authData.put("password", userDataWithEmail.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests.makePostRequest(getUrlLogin(), authData);

        //Edit with wrong id
        String newName = "C";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithHeaderAndCookie(
                getUrlAuth() + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"The value for field `firstName` is too short\"}");
    }
}
