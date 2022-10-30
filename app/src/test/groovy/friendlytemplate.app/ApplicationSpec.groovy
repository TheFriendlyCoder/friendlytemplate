package friendlytemplate.app

import picocli.CommandLine
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.TempDir
import spock.lang.Title

import java.nio.file.Path
import java.nio.file.Paths

@Title("Tests basic command line interface")
@Subject(App)
class ApplicationSpec extends Specification {
    @TempDir
    Path tempDir

    def "Show version output"() {
        given: "A command line interface with a parameter requesting the application version"
        String[] args = ["--version"]
        CommandLine cmd = App.defaultCommandLineSpec(args)

        StringWriter sw = new StringWriter()
        cmd.setOut(new PrintWriter(sw))

        when: "Executing application via command line interface"
        int exitCode = cmd.execute(args);

        then: "Operation should display the app version to standard output"
        exitCode == 0
        // NOTE: the version number for the package currently doesn't get
        // loaded properly from the app when it is run from within the local
        // test environment
        sw.toString().strip() == "friendlytemplate version null"
    }

    def "Process basic template"() {
        given: "A path to a fully valid source template"
        Path templateDir = TestUtils.getPath("simpleExample")

        and: "A command line interface with all required parameters to process the template"
        String[] args = [templateDir.toString(), tempDir.toString(), "--package_name=testproj"]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter sw = new StringWriter()
        cmd.setOut(new PrintWriter(sw))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = sw.toString()
        println("Folder contents: " + tempDir.toFile().listFiles())

        then: "Exit code should indicate success"
        exitCode == 0

        and: "Template files should be pre-processed and copied to target folder"
        tempDir.resolve("project.prop").toFile().exists()
        tempDir.resolve(Paths.get("src", "testproj", "version.txt")).toFile().exists();

        and: "Status inforamtion should be shown on standard output"
        with (output) {
            contains(templateDir.toString())
            contains(tempDir.toString())
            contains("package_name")
        }
    }

    void "Missing template folder"() {
        given: "A source and target folder but only the target folder exists"
        Path templateDir = tempDir.resolve("source")
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdir()

        and: "A command line parser pointing to the missing template folder"
        String[] args = [templateDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then: "Application should fail with a sensible exit code and error message"
        exitCode == -1
        with (output) {
            contains("folder doesn't exist")
            contains(templateDir.toString())
        }
    }

    void "Missing template config file"() {
        given: "A source folder without a template config file in it"
        Path templateDir = tempDir.resolve("source")
        templateDir.toFile().mkdir()
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdir()

        and: "A command line parser pointing to the empty template folder"
        String[] args = [templateDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then: "Application should fail with a sensible exit code and error message"
        exitCode == -1
        with (output) {
            contains("Unable to read config file")
            contains(templateDir.toString())
        }
    }

    void "Missing required target parameter"() {
        given: "The path to a valid template folder"
        Path sourceDir = TestUtils.getPath("simpleExample")

        and: "A command line parser pointing to the template folder without an output folder "
        String[] args = [sourceDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then: "Application should succeed and show the tool default help message"
        exitCode == 0
        with (output) {
            contains("friendlytemplate")
            contains("Usage")
        }
    }

    void "Show usage message when no parameters given"() {
        given: "A command line parser with no input parameters"
        String[] args = []
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then: "Application should complete successfully and display the default help message"
        exitCode == 0
        with (output) {
            contains("friendlytemplate")
            contains("Usage")
        }

    }

    void "Target folder does not exist"() {
        given: "A valid template folder and a non-existent output folder"
        Path sourceDir = TestUtils.getPath("simpleExample")
        Path targetDir = tempDir.resolve("target")

        and: "A command line parser pointing to the template folder and non existent target"
        String[] args = [sourceDir.toString(), targetDir.toString(), "--package_name=testproj"]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stderr = new StringWriter()
        cmd.setErr(new PrintWriter(stderr))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stderr.toString()

        then: "Operation should fail with a sensible exit code and error message"
        exitCode == -1
        with (output) {
            contains(targetDir.toString())
            contains("folder doesn't exist")
        }
    }

    void "CLI should prompt user for missing field parameters"() {
        given: "A valid source template and empty target folder"
        Path sourceDir = TestUtils.getPath("simpleExample")
        Path targetDir = tempDir.resolve("target")
        targetDir.toFile().mkdirs()

        and: "A command line parser pointing to the template and target folder"
        String[] args = [sourceDir.toString(), targetDir.toString()]
        CommandLine cmd = App.defaultCommandLineSpec(args)
        StringWriter stdout = new StringWriter()
        cmd.setOut(new PrintWriter(stdout))

        when: "Executing the app via the command line"
        int exitCode = cmd.execute(args)
        String output = stdout.toString()

        then: "The app should complete successfully but prompt the user for the missing values"
        exitCode == 0
        output.contains("package_name")
    }
}