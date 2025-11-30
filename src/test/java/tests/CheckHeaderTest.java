package tests;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CheckHeaderTest {

    @Test
    public void CheckHeaderTest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        Headers headers = response.getHeaders();

        for (Header header: headers) {
            String nameHeader = header.getName();
            String valueHeader = header.getValue();

            assertNotNull(headers, "Хедеров нет в ответе на запрос");
            assertEquals(
                    valueHeader,
                    response.getHeader(nameHeader),
                    "Хедера с именем " + nameHeader + " нет в ответе запроса"
            );
        }
    }
}
