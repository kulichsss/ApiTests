package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a POST-request with body")
    public Response makePostRequest(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make GET-request with header and cookie")
    public Response makeGetRequestWithHeaderAndCookie(String url, String header, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make PUT-request with header and cookie and body")
    public Response makePutRequestWithHeaderAndCookie(String url, String header, String cookie, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .body(userData)
                .put(url)
                .andReturn();
    }

    @Step("Make DELETED-request with header and cookie")
    public Response makeDeletedRequestWithHeaderAndCookie(String url, String header, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .delete(url)
                .andReturn();
    }

    @Step("Make PUT-request without header and cookie")
    public Response makePutRequestWithHeaderAndCookie(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .put(url)
                .andReturn();
    }

    @Step("Make a jsonPath POST-request with body")
    public JsonPath makeJsonPostRequest(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .jsonPath();
    }
}
