package lib;

import io.qameta.allure.Step;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataGenerator {

    @Step("Создаём рандомный email")
    public static String getRandomEmail() {
        String genEmail = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        return "Kul" + genEmail + "@example.com";
    }

    @Step("Создаём некорректный email")
    public static String getWrongEmail(int n) {
        var rnd = new Random();
        Supplier<Integer> randomNumber = () -> rnd.nextInt(26);
        String result = Stream.generate(randomNumber)
                .limit(n)
                .map(i -> 'a' + i)
                .map(Character::toString)
                .collect(Collectors.joining());
        return result + "example.com";
    }

    @Step("Создаём рандомное firstName")
    public static String getFirstName(int n) {
        var rnd = new Random();
        Supplier<Integer> randomNumber = () -> rnd.nextInt(26);
        String firstName = Stream.generate(randomNumber)
                .limit(n)
                .map(i -> 'a' + i)
                .map(Character::toString)
                .collect(Collectors.joining());
        return firstName;
    }

    @Step("Создаём данные для регистрации пользователя")
    public static Map<String, String> getRegistrationData() {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("email", getRandomEmail());
        registrationData.put("password", "123");
        registrationData.put("username", "kul");
        registrationData.put("firstName", "kul");
        registrationData.put("lastName", "kul");

        return registrationData;
    }

    @Step("Создаём данные для регистрации пользователя на основе имеющихся данных")
    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValue) {
        Map<String, String> defaultData = getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key: keys) {
            if (nonDefaultValue.containsKey(key)) {
                userData.put(key, nonDefaultValue.get(key));
            } else {
                userData.put(key, defaultData.get(key));
            }
        }
        return userData;
    }

    @Step("Создаём данные для регистрации пользователя без поля: {field}")
    public static Map<String, String> getRegistrationDataWithoutOneField(String field) {
        Map<String, String> defaultData = getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};

        for (String key: keys) {
            if (!key.equals(field)) {
                userData.put(key, defaultData.get(key));
            }
        }
        return userData;
    }
}
