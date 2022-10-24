package friendlytemplate.app;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

/**
 * Abstraction around a FriendlyTemplate configuration file.
 */
public class ConfigFile {
    private int templateVersion;
    private Map<String, List<String>> main;

    private Path templateDir;
    /**
     * Constructor.
     *
     * @param sourceData input stream containing YAML configuration data for
     *                   the template being processed
     */
    static ConfigFile fromYaml(InputStream sourceData) {
        // Ensure our yaml file can be deserialized into the data members
        // of this class
        Yaml yaml = new Yaml(new Constructor(ConfigFile.class));
        // Give the YAML library permission to populate the private
        // data members of this class
        yaml.setBeanAccess(BeanAccess.FIELD);

        // Load the YAML file, deserializing the contents into an instance
        // of the ConfigFile class
        return yaml.load(sourceData);
    }

    public static ConfigFile fromYaml(File sourceFile) throws FileNotFoundException {
        FileInputStream temp = new FileInputStream(sourceFile);
        ConfigFile retval = fromYaml(temp);
        retval.templateDir = sourceFile.getParentFile().toPath();
        return retval;
    }
    public int getTemplateVersion() {
        return templateVersion;
    }

    public List<String> getFieldNames() {
        return main.get("fields");
    }

    public List<Path> getSourceFiles() {
        return main.get("files").stream().map(Paths::get).collect(Collectors.toList());
    }

    public Path getTemplateDir() {
        return templateDir;
    }
}
