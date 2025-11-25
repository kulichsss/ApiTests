import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
}
