package friendlytemplate.app

import picocli.CommandLine
import spock.lang.Specification
import spock.lang.TempDir
import java.nio.file.Path


class AppTests extends Specification {
    @TempDir
    Path tempDir

    def "Show version output"() {
        given:
        String[] args = ["--version"]
        CommandLine cmd = App.defaultCommandLineSpec(args)

        StringWriter sw = new StringWriter()
        cmd.setOut(new PrintWriter(sw))

        when:
        int exitCode = cmd.execute(args);

        then:
        exitCode == 0
        // NOTE: the version number for the package currently doesn't get
        // loaded properly from the app when it is run from within the local
        // test environment
        sw.toString().strip() == "friendlytemplate version null"
    }

    def "Process basic template"() {
        given:
        String templateDir = getClass().getClassLoader().getResource("simpleExample").getPath()
        String[] args = [templateDir, tempDir.toString(), "--project_name=testproj"]

        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter sw = new StringWriter()
        cmd.setOut(new PrintWriter(sw))

        when:
        int exitCode = cmd.execute(args)
        String output = sw.toString()

        then:
        exitCode == 0
        output.contains(templateDir)
        output.contains(tempDir.toString())
        output.contains("project_name")
    }

    void "Missing template folder"() {
        given:
        Path sourceDir = tempDir.resolve("source")
        sourceDir.toFile().mkdir()
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdir()

        String[] args = [sourceDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when:
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then:
        exitCode == -1
        output.contains("not found")
        output.contains(sourceDir.toString())
    }
}