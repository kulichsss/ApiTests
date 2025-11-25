import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HelloWorldTest {
    @Test
    public void helloWorldTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void PrintHelloWorldTest() {
        System.out.println("Hello from Danila Kulikov");
    }

    @Test
    public void getTextTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        System.out.println(response.getBody().asString());
    }

    @Test
    public void PrintSecondTextTest() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondText = response.get("messages[1].message");
        System.out.println(secondText);
    }

    @Test
    public void PrintRedirectUrlTest() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void LongRedirectTest() {
        int countRedirects = 0;
        String currentUrl = "https://playground.learnqa.ru/api/long_redirect";
        while (!(currentUrl == null)) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(currentUrl)
                    .andReturn();
            countRedirects++;
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                System.out.println(currentUrl);
                System.out.println("Количество редиректов "+ countRedirects);
            }
            if (currentUrl == null) {
                break;
            }
            currentUrl = response.getHeader("Location");
        }
    }

    @Test
    public void TokenJobTest() throws InterruptedException {
        String currentUrl = "https://playground.learnqa.ru/ajax/api/longtime_job";

        JsonPath response = RestAssured
                .get(currentUrl)
                .jsonPath();
        String token = response.get("token");
        int time = response.get("seconds");
        System.out.println(time);

        if (time != 0) {
            JsonPath errorResponse = RestAssured
                    .given()
                    .queryParam("token", token)
                    .when()
                    .get(currentUrl)
                    .jsonPath();
            String status = errorResponse.get("status");

            if (status.equals("Job is NOT ready")) {
                System.out.println(status);
            }
            else {
                System.out.println("Время вышло, задача выполнилась, тест провален!");
            }
        }
        Thread.sleep(time * 1000L);
        JsonPath successResponse = RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get(currentUrl)
                .jsonPath();
        String status = successResponse.get("status");
        String result = successResponse.get("result");
        if (status.equals("Job is ready")) {
            System.out.println(status);
            System.out.println(result);
        }
        else {
            System.out.println("Задача еще не выполнена, нужен еще один запрос");
        }
    }

    @Test
    public void AuthTest() {
        String authCookieUrl = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String checkAuthCookieUrl = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
        String login = "super_admin";

        DataProvide dataProvide = new DataProvide();
        Set<String> passwords = dataProvide.passwords();

        for (String password: passwords) {
            Response authResponse = RestAssured
                    .given()
                    .formParam("login", login)
                    .formParam("password", password)
                    .when()
                    .post(authCookieUrl)
                    .andReturn();
            String cookie = authResponse.getCookie("auth_cookie");

            Map<String, String> authCookie = new HashMap<>();
            authCookie.put("auth_cookie", cookie);

            if (!cookie.isEmpty()) {
                Response checkResponse = RestAssured
                        .given()
                        .formParam("login", login)
                        .formParam("password", password)
                        .cookies(authCookie)
                        .when()
                        .post(checkAuthCookieUrl)
                        .andReturn();
                String authCheckText = checkResponse.getBody().asString();

                if (authCheckText.equals("You are authorized")) {
                    System.out.println(authCheckText);
                    System.out.println(password);
                    break;
                }
            }
        }
    }
}
