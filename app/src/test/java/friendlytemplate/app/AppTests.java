package friendlytemplate.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTests {

    @Test
    void testAppVersion() throws Exception {
        String[] args = new String[] {"--version"};
        CommandLine cmd = App.defaultCommandLineSpec(args);

        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);
        // NOTE: the version number for the package currently doesn't get
        // loaded properly from the app when it is run from within the local
        // test environment
        assertEquals("friendlytemplate version null", sw.toString().strip());
    }

    @Test
    void testBasicTemplate(@TempDir Path tempDir) throws Exception {

        String templateDir = getClass().getClassLoader().getResource("simpleExample").getPath();
        String[] args = new String[] {templateDir, tempDir.toString(), "--project_name=testproj"};

        CommandLine cmd = App.defaultCommandLineSpec(args);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        // Flex production code
        int exitCode = cmd.execute(args);

        // Make sure or operation completes successfully
        assertEquals(0, exitCode);

        // Make sure the input and output paths are mentioned in the stdout
        assertThat(sw.toString(), containsString(templateDir));
        assertThat(sw.toString(), containsString(tempDir.toString()));
        assertThat(sw.toString(), containsString("project_name"));
    }

    @Test
    void testMissingTemplateFile(@TempDir Path tempDir) throws Exception {
        // Make an empty source folder which doesn't contain a template
        // file defining the project template
        Path sourceDir = tempDir.resolve("source");
        sourceDir.toFile().mkdir();
        Path targetDir = tempDir.resolve("target");
        targetDir.toFile().mkdir();

        String[] args = new String[] {sourceDir.toString(), targetDir.toString()};
        CommandLine cmd = App.defaultCommandLineSpec(args);
        StringWriter stderr = new StringWriter();
        cmd.setErr(new PrintWriter(stderr));

        // Flex production code
        int exitCode = cmd.execute(args);

        // Make sure command fails
        assertEquals(-1, exitCode);

        // Make sure error output has a useful error message
        assertThat(stderr.toString(), containsString("not found"));
        assertThat(stderr.toString(), containsString(sourceDir.toString()));
    }

}
