package friendlytemplate.app;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTests {
    @Test
    void testAppMessage() {
        App app = new App();
        CommandLine cmd = new CommandLine(app);

        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int exitCode = cmd.execute("fubar1", "fubar2");
        assertEquals(0, exitCode);
        assertThat(sw.toString(), containsString("Generating project"));
    }
}
