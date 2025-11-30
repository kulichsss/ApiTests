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
        String actualValue = cookies.get("HomeWork");
        assertEquals("hw_value", actualValue, "Неверное значение куки HomeWork");
    }
}
