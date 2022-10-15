package friendlytemplate.app;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

/**
 * Abstraction around a FriendlyTemplate configuration file.
 */
public class ConfigFile {
    private int templateVersion;
    private Map<String, List<String>> main;

    /**
     * Constructor.
     *
     * @param sourceData input stream containing YAML configuration data for
     *                   the template being processed
     */
    public static ConfigFile fromYaml(InputStream sourceData) {
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

    public int getTemplateVersion() {
        return templateVersion;
    }

    public List<String> getFieldNames() {
        return main.get("fields");
    }
}
