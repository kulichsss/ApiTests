import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JUnitTests {

    @Test
    public void ShortTextPrintTest() {
        String hello = "Hello, world";
        int length = hello.length();
        assertTrue( length <=15, "Ожидаемая длина <= 15, но фактическая длина '" + hello + "' = " + length);
    }
}
