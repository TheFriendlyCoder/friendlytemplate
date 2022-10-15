package friendlytemplate.app;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigFileTests {
    @Test
    void testParseConfigVersion() {
        InputStream initialStream = new ByteArrayInputStream("templateVersion: 2".getBytes());
        ConfigFile cfg = ConfigFile.fromYaml(initialStream);
        assertEquals(2, cfg.getTemplateVersion());
    }

    @Test
    void testParseSampleConfigFile() {
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml");
        ConfigFile cfg = ConfigFile.fromYaml(sample_file);
        assertEquals(1, cfg.getTemplateVersion());
    }

    @Test
    void testParseFieldNames() {
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml");
        ConfigFile cfg = ConfigFile.fromYaml(sample_file);
        List<String> values = cfg.getFieldNames();

        assertEquals(1, values.size());
        assertEquals("project_name", values.get(0));
    }
}
