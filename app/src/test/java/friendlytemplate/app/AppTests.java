package friendlytemplate.app;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTests {

    @Test
    void testAppVersion() {
        App app = new App();
        CommandLine cmd = new CommandLine(app);

        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int exitCode = cmd.execute("--version");
        assertEquals(0, exitCode);
        // NOTE: the version number for the package currently doesn't get
        // loaded properly from the app when it is run from within the local
        // test environment
        assertEquals("friendlytemplate version null", sw.toString().strip());
    }

}
