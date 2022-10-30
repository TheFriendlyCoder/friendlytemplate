package friendlytemplate.app

import picocli.CommandLine
import spock.lang.Specification
import spock.lang.TempDir
import java.nio.file.Path


class ApplicationSpec extends Specification {
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
        String[] args = [templateDir, tempDir.toString(), "--package_name=testproj"]

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
        output.contains("package_name")
    }

    void "Missing template folder"() {
        given:
        Path templateDir = tempDir.resolve("source")
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdir()

        String[] args = [templateDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when:
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then:
        exitCode == -1
        output.contains("folder doesn't exist")
        output.contains(templateDir.toString())
    }

    void "Missing template config file"() {
        given:
        Path templateDir = tempDir.resolve("source")
        templateDir.toFile().mkdir()
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdir()

        String[] args = [templateDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when:
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then:
        exitCode == -1
        output.contains("Unable to read config file")
        output.contains(templateDir.toString())
    }

    void "Missing required target parameter"() {
        given:
        String sourceDir = getClass().getClassLoader().getResource("simpleExample").getPath()

        String[] args = [sourceDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when:
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then:
        exitCode == 0
        output.contains("friendlytemplate")
        output.contains("Usage")
    }

    void "Show usage message when no parameters given"() {
        given:
        String[] args = []
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when:
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then:
        exitCode == 0
        output.contains("friendlytemplate")
        output.contains("Usage")
    }

    void "Target folder does not exist"() {
        given:
        String sourceDir = getClass().getClassLoader().getResource("simpleExample").getPath()
        Path targetDir = tempDir.resolve("target")

        String[] args = [sourceDir.toString(), targetDir.toString(), "--package_name=testproj"]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when:
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then:
        exitCode == -1
        output.contains(targetDir.toString())
        output.contains("folder doesn't exist")
    }

    void "CLI should prompt user for missing field parameters"() {
        given:
        String sourceDir = getClass().getClassLoader().getResource("simpleExample").getPath()
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdirs()

        String[] args = [sourceDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when:
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then:
        exitCode == 0
        output.contains("package_name")
    }
}