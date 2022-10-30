package friendlytemplate.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

/** Abstraction around a FriendlyTemplate configuration file. */
public class ConfigFile {
    private int templateVersion;
    private Map<String, List<String>> main;

    private Path templateDir;

    /**
     * Constructor.
     *
     * @param sourceFile Path to the config file to be parsed
     * @return instance of the newly constructed class
     * @throws FileNotFoundException if the config file is not found
     */
    public static ConfigFile fromYaml(File sourceFile) throws FileNotFoundException {
        FileInputStream sourceData = new FileInputStream(sourceFile);

        // Ensure our yaml file can be deserialized into the data members
        // of this class
        Yaml yaml = new Yaml(new Constructor(ConfigFile.class));
        // Give the YAML library permission to populate the private
        // data members of this class
        yaml.setBeanAccess(BeanAccess.FIELD);
        // Load the YAML file, deserializing the contents into an instance
        // of the ConfigFile class
        ConfigFile retval = yaml.load(sourceData);

        retval.templateDir = sourceFile.getParentFile().toPath().toAbsolutePath();
        return retval;
    }

    public int getTemplateVersion() {
        return templateVersion;
    }

    public List<String> getFieldNames() {
        return main.get("fields");
    }

    public List<File> getSourceFiles() {
        return main.get("files").stream().map(item -> Paths.get(templateDir.toString(), item).toFile()).toList();
    }

    public Path getTemplateDir() {
        return templateDir;
    }

}
