package friendlytemplate.app;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Abstraction around a FriendlyTemplate configuration file.
 */
public class ConfigFile {
    private Map<String, Object> data;
    private int templateVersion;

    /**
     * Constructor.
     *
     * @param sourceData input stream containing YAML configuration data for
     *                   the template being processed
     */
    public ConfigFile(InputStream sourceData) {
        Yaml yaml = new Yaml();
        data = yaml.load(sourceData);
        templateVersion = (int) data.get("template_version");
    }

    public int getTemplateVersion() {
        return templateVersion;
    }

}
