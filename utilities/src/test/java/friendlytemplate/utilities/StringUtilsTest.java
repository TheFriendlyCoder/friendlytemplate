package friendlytemplate.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {
    @Test
    void testSplit() {
        var result = StringUtils.split("Hello World");
        assertEquals(2, result.size());
    }

    @Test
    void testJoin() {
        var temp = StringUtils.split("Hello World");
        var result = StringUtils.join(temp);
        assertEquals(result, "Hello World");
    }
}
