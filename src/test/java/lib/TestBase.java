package lib;

public class TestBase {
    protected String getBaseUrl() {
        String env = System.getProperty("env", "dev"); // Дефолтное окружение prod

        switch (env.toLowerCase()) {
            case "dev":
                return "https://playground.learnqa.ru/api_dev/user/";
            case "prod":
            default:
                return "https://playground.learnqa.ru/api/user/";
        }
    }

    protected String getUrlAuth() {
        return getBaseUrl();
    }

    protected String getUrlLogin() {
        return getBaseUrl() + "login";
    }
}
