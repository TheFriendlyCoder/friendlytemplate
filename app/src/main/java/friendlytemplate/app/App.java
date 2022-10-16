package friendlytemplate.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ConfigFile configFile = ConfigFile.fromYaml(
                new FileInputStream(configFilePath.toFile()));
        assert configFile.getTemplateVersion() == 1;

        // Load all fields for the template from the config file and see if
        // the user has provided parameters for them on the command line or if
        // we need to load them from some other means
        // TODO: add support for prompting user for field values
        // TODO: add support for user to provide CLI parameters for each field
        // TODO: add some type of online help for fields
        configFile.getFieldNames().forEach(field -> {
            spec.commandLine().getOut().println("Processing field " + field);
            spec.addOption(CommandLine.Model.OptionSpec.builder("--" + field)
                    .paramLabel("PROJECTNAME")
                    .type(String.class)
                    .description("Name of the project").build());
        });
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
        System.out.println(args[0]);
        /*CommandLine app = new CommandLine(new App());
        // TODO: use this operation to customize the error output from
        //       exceptions (ie: to avoid stack traces on the console)
        //app.setExecutionExceptionHandler();
        int exitCode = app.execute(args);
        System.exit(exitCode);*/

        CommandSpec spec = CommandSpec.create();
        spec.mixinStandardHelpOptions(true);

        CommandLine commandLine = new CommandLine(spec);
        commandLine.setExecutionStrategy(App::run);
        spec.addPositional(CommandLine.Model.PositionalParamSpec.builder()
                .paramLabel("SOURCE")
                .type(Path.class)
                .description("The source folder containing the template")
                .build());
        //CommandLine.ParseResult pr = commandLine.parseArgs(args);
        //Path sourcePath = pr.matchedPositionalValue(0, Paths.get(".").toAbsolutePath());
        Path sourcePath = new File(args[0]).toPath().toAbsolutePath();
        Path configFilePath = sourcePath.resolve("friendly.template.yml");
        if (configFilePath.toFile().exists()) {
            try {
                ConfigFile configFile = ConfigFile.fromYaml(
                        new FileInputStream(configFilePath.toFile()));
                configFile.getFieldNames().forEach(field -> {
                    spec.addOption(CommandLine.Model.OptionSpec.builder("--" + field)
                            .paramLabel(field)
                            .type(String.class)
                            .description("Name of the project")
                            .interactive(true)
                            .prompt("Give the project name")
                            .required(true)
                            .required(true)
                            .build());
                });
            } catch (FileNotFoundException err) {
                System.out.println("1Config file not found");
                return;
            }
        }
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);

    }
    static int run (CommandLine.ParseResult pr) {
        Integer helpExitCode = CommandLine.executeHelpRequest(pr);
        if (helpExitCode != null) { return helpExitCode; }

        //System.out.println(pr.matchedPositionals().get(0).paramLabel());
        Path sourcePath = pr.matchedPositionalValue(0, Paths.get(".").toAbsolutePath());
        Path configFilePath = sourcePath.resolve("friendly.template.yml");
        if (!configFilePath.toFile().exists()) {
            //throw new FileNotFoundException("Config file " + configFilePath + " not found");
            System.out.println("File not found: " + configFilePath);
            return -1;
        }
        try {
            ConfigFile configFile = ConfigFile.fromYaml(
                    new FileInputStream(configFilePath.toFile()));
            System.out.println("Template version " + configFile.getTemplateVersion());

            Map<String, String> params = new HashMap<>();
//            pr.matchedPositionals().forEach(arg-> {
//                if (!configFile.getFieldNames().contains(arg.paramLabel())) {
//                    return;
//                }
//                params.put(arg.paramLabel(), arg.getValue());
//            });
            List<String> allFields = configFile.getFieldNames();
            allFields.forEach(fieldName-> {
                if (pr.hasMatchedOption(fieldName)) {
                    CommandLine.Model.OptionSpec newValue = pr.matchedOption(fieldName);
                    System.out.println("Value for field is " + newValue.getValue());
                    params.put(fieldName, newValue.getValue());
                }
            });
            System.out.println(params);
        } catch (FileNotFoundException err) {
            System.out.println("Config file not found");
            return -1;
        }

        return 0;
    }
}
