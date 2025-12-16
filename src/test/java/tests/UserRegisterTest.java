package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static lib.Assertions.assertResponseTextEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Authorisation cases")
@Feature("Authorization")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test with wrong user email")
    @Description("This test wrong authorization")
    public void createUserWithWrongEmail() {
        String wrongEmail = DataGenerator.getWrongEmail(7);

        Map<String, String> userWrongEmail = new HashMap<>();
        userWrongEmail.put("email", wrongEmail);

        Response responseCreateWrongAuth = apiCoreRequests
                .makePostRequest(getUrlAuth(), DataGenerator.getRegistrationData(userWrongEmail));

        assertEquals("Invalid email format", responseCreateWrongAuth.asString());
    }

    @ParameterizedTest
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test with missing fields")
    @Description("This test incorrect authorization")
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void createUserWithoutOneField(String missingField) {
        Response responseCreateIncorrectAuth = apiCoreRequests
                .makePostRequest(getUrlAuth(), DataGenerator.getRegistrationDataWithoutOneField(missingField));

        assertResponseTextEquals(responseCreateIncorrectAuth, "The following required params are missed: " + missingField);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test with short firstname")
    @Description("This test incorrect authorization")
    public void createUserWithShortName() {
        String shortFirstName = DataGenerator.getFirstName(1);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", shortFirstName);

        Response responseAuthShortName = apiCoreRequests
                .makePostRequest(getUrlAuth(), DataGenerator.getRegistrationData(userData));

        assertResponseTextEquals(responseAuthShortName, "The value of 'firstName' field is too short");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test with too long firstname")
    @Description("This test incorrect authorization")
    public void createUserWithLongName() {
        String longFirstName = DataGenerator.getFirstName(251);

        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", longFirstName);

        Response responseAuthShortName = apiCoreRequests
                .makePostRequest(getUrlAuth(), DataGenerator.getRegistrationData(userData));

        assertResponseTextEquals(responseAuthShortName, "The value of 'firstName' field is too long");
    }

}
