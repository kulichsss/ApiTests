package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserAgentTest {

    private static final String USER = "В User-Agent = ";
    private static final String SERVER = ", а сервер распознал как ";

    private static Stream<Arguments> dataProvide() {
        return Stream.of(
                Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30", "Mobile", "No", "Android"),
                Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1", "Mobile", "Chrome", "iOS"),
                Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)", "Googlebot", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0", "Web", "Chrome", "No"),
                Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1", "Mobile", "No", "iPhone")
        );
    };

    @MethodSource("dataProvide")
    @ParameterizedTest(name = "'platform': {1}, 'browser': {2}, 'device': {3}")
    public void userAgentTest(String userAgent, String platform, String browser, String device) {
        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        JsonPath json = response.jsonPath();
        Assertions.assertEquals(platform, json.getString("platform"), USER + userAgent + ", ожидали увидеть platform = " + platform + SERVER + json.getString("platform"));
        Assertions.assertEquals(browser, json.getString("browser"), USER + userAgent + " ,ожидали увидеть browser = " + browser + SERVER + json.getString("browser"));
        Assertions.assertEquals(device, json.getString("device"), USER + userAgent + " ,ожидали увидеть device = " + device + SERVER + json.getString("device"));
    }
}
