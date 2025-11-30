package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CheckCookieTest {

    @Test
    public void CheckCookieTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String> cookies = response.getCookies();

        assertNotNull(cookies, "Куков нет в ответе на запрос");
        for (Map.Entry<String, String> cookie: cookies.entrySet()) {
            String cookieKey = cookie.getKey();
            String cookieValue = cookie.getValue();

            assertEquals(cookieValue, response.getCookie(cookieKey), "Неверное значение куки " + cookieKey);
        }
    }
}
