package friendlytemplate.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

class App {

    final Logger logger = LoggerFactory.getLogger(App.class);

    private static String getVersion() {
        String version = App.class.getPackage().getImplementationVersion();
        return "${COMMAND-FULL-NAME} version " + version;
    }

    /*@Override
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

        return 0;
    }*/

    public static CommandLine defaultCommandLineSpec(String... args) throws FileNotFoundException {
        // Setup standard command spec for our app
        CommandSpec spec =
                CommandSpec.create()
                        .name("friendlytemplate")
                        .version(getVersion())
                        .mixinStandardHelpOptions(true);
        spec.usageMessage()
                .description("Produces source code projects based on a template definition");
        spec.addPositional(
                CommandLine.Model.PositionalParamSpec.builder()
                        .paramLabel("SOURCE")
                        .type(Path.class)
                        .description("The source folder containing the template")
                        .build());
        spec.addPositional(
                CommandLine.Model.PositionalParamSpec.builder()
                        .paramLabel("TARGET")
                        .type(Path.class)
                        .description("Folder where the new project is to be created")
                        .build());
        CommandLine commandLine = new CommandLine(spec);

        // Set entrypoint method
        commandLine.setExecutionStrategy(App::run);

        // Extend command interface with options for each of the parameters
        // needed by the template being loaded
        commandLine.setUnmatchedArgumentsAllowed(true);
        CommandLine.ParseResult pr = commandLine.parseArgs(args);
        commandLine.setUnmatchedArgumentsAllowed(false);
        if (!pr.hasMatchedPositional(0)) {
            return commandLine;
        }

        CommandLine.Model.PositionalParamSpec sourcePathOption = pr.matchedPositional(0);
        assert sourcePathOption.paramLabel().equals("SOURCE");

        Path sourcePath = sourcePathOption.getValue();
        Path configFilePath = sourcePath.toAbsolutePath().resolve("friendly.template.yml");
        if (!configFilePath.toFile().exists()) {
            System.err.println("File not found: " + configFilePath);
            return commandLine;
        }
        ConfigFile configFile;

        configFile = ConfigFile.fromYaml(new FileInputStream(configFilePath.toFile()));

        configFile
                .getFieldNames()
                .forEach(
                        field -> {
                            commandLine
                                    .getCommandSpec()
                                    .addOption(
                                            CommandLine.Model.OptionSpec.builder("--" + field)
                                                    .paramLabel(field)
                                                    .type(String.class)
                                                    .description("Name of the project")
                                                    .interactive(true)
                                                    .build());
                        });

        return commandLine;
    }

    public static void main(String... args) throws Exception {
        // Set up logging interface
        // See the simplelogger.properties file in the resources section for
        // more config options
        // System.setProperty(
        // org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        // TODO: use this operation to customize the error output from
        //       exceptions (ie: to avoid stack traces on the console)
        // https://picocli.info/picocli-programmatic-api.html#_parsing_and_result_processing

        CommandLine commandLine = defaultCommandLineSpec(args);
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    static int run(CommandLine.ParseResult pr) {
        // Start by processing online help
        Integer helpExitCode = CommandLine.executeHelpRequest(pr);
        if (helpExitCode != null) {
            return helpExitCode;
        }
        CommandLine commandLine = pr.commandSpec().commandLine();
        if (!pr.hasMatchedPositional(0) || !pr.hasMatchedPositional(1)) {
            commandLine.usage(commandLine.getOut());
            return 0;
        }

        // Next, parse the template config file from the source folder
        Path sourcePath = pr.matchedPositional(0).getValue();
        commandLine.getOut().println("Processing template " + sourcePath);
        if (!sourcePath.toFile().exists()) {
            commandLine.getErr().println("Template folder doesn't exist: " + sourcePath);
            return -1;
        }

        Path configFilePath = sourcePath.resolve("friendly.template.yml");
        ConfigFile configFile;
        try {
            configFile = ConfigFile.fromYaml(new FileInputStream(configFilePath.toFile()));
        } catch (FileNotFoundException err) {
            commandLine.getErr().println("Unable to read config file: " + configFilePath);
            return -1;
        }
        commandLine.getOut().println("Template version " + configFile.getTemplateVersion());

        // Generate a hash map of all the required fields from the template,
        // prompting the user for any missing values
        Map<String, String> params = new HashMap<>();
        List<String> allFields = configFile.getFieldNames();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        allFields.forEach(
                fieldName -> {
                    if (pr.hasMatchedOption(fieldName)) {
                        CommandLine.Model.OptionSpec newValue = pr.matchedOption(fieldName);
                        commandLine.getOut().println("Value for field is " + newValue.getValue());
                        params.put(fieldName, newValue.getValue());
                    } else {
                        commandLine.getOut().print("Provide value for " + fieldName + ":");
                        try {
                            String value = br.readLine();
                            params.put(fieldName, value);
                        } catch (IOException err) {
                            commandLine.getErr().println("Error: " + err);
                        }
                    }
                });

        Path targetPath = pr.matchedPositional(1).getValue();
        if (!targetPath.toFile().exists()) {
            commandLine.getErr().println("Target folder doesn't exist: " + targetPath);
            return -1;
        }

        commandLine.getOut().println(params);
        commandLine.getOut().println("Generating project in " + targetPath);
        return 0;
    }
}
