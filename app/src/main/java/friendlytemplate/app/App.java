package friendlytemplate.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
//import org.yaml.snakeyaml.Yaml;
//import org.eclipse.jgit.api.Git;
//import picocli.CommandLine.Option;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.math.BigInteger;
//import java.nio.file.Files;
//import java.security.MessageDigest;
//import java.util.Map;


@Command(name = "friendlytemplate", mixinStandardHelpOptions = true,
        versionProvider = App.AppVersionProvider.class,
        description =
                "Produces source code projects based on a template definition")
class App implements Callable<Integer> {

    final Logger logger = LoggerFactory.getLogger(App.class);

    @Spec CommandSpec spec;

    @Parameters(
            index = "0",
            description = "Source folder containing the template")
    private Path sourcePath;

    @Parameters(
            index = "1",
            description = "Output folder where the new source project is to "
                    + "be generated")
    private Path destPath;

    /**
     * Helper class that loads version info from the app package.
     */
    static class AppVersionProvider implements CommandLine.IVersionProvider {
        public String[] getVersion() {
            String version = App.class.getPackage().getImplementationVersion();
            return new String[] { "${COMMAND-FULL-NAME} version " + version };
        }
    }

    @Override
    public Integer call() throws Exception {
        spec.commandLine().getOut().println("Running from " + sourcePath
                + " to " + destPath + "...");
        logger.debug("Generating project...");

        Path configFilePath = sourcePath.resolve("friendly.template.yml");
        if (!configFilePath.toFile().exists()) {
            throw new FileNotFoundException("Config file " + configFilePath + " not found");
        }
        ConfigFile configFile = new ConfigFile(
                new FileInputStream(configFilePath.toFile()));
        assert configFile.getTemplateVersion() == 1;

        /*String repo = "https://github.com/TheFriendlyCoder/friendlytemplate";
        Path outPath = Files.createTempDirectory("friendlytemplate");
        System.out.println("Output folder is " + outPath);
        Git git = Git.cloneRepository()
                .setURI(repo)
                .setDirectory(outPath.toFile())
                .setNoCheckout(true)
                .call();
        // Shallow checkout testing...
        git.checkout().addPath("/folder/in/repo")
        System.out.println(git.toString());

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(file.getAbsolutePath());
        InputStream inputStream = new FileInputStream(file);
        Map<String, Object> obj = yaml.load(inputStream);
        Object obj = yaml.load(inputStream);
        System.out.println(obj);

        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] digest = MessageDigest.getInstance(algorithm).
        digest(fileContents);
        System.out.printf("%0" + (digest.length*2) + "x%n",
        new BigInteger(1, digest));
         */
        return 0;
    }

    public static void main(String... args) {
        // Set up logging interface
        // See the simplelogger.properties file in the resources section for
        // more config options
        // System.setProperty(
        // org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        CommandLine app = new CommandLine(new App());
        // TODO: use this operation to customize the error output from
        //       exceptions (ie: to avoid stack traces on the console)
        //app.setExecutionExceptionHandler();
        int exitCode = app.execute(args);
        System.exit(exitCode);


    }
}
