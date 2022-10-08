package friendlytemplate.app;
import org.eclipse.jgit.api.Git;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.Callable;


@Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
class App implements Callable<Integer> {

    @Parameters(index = "0", description = "The file whose checksum to calculate.")
    private File file;

    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-256";

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.println("Checking out sample...");
        String repo = "https://github.com/TheFriendlyCoder/friendlytemplate";
        Path outPath = Files.createTempDirectory("friendlytemplate");
        System.out.println("Output folder is " + outPath);
        Git git = Git.cloneRepository()
                .setURI(repo)
                .setDirectory(outPath.toFile())
                .call();
        System.out.println(git.toString());

        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
        System.out.printf("%0" + (digest.length*2) + "x%n", new BigInteger(1, digest));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
