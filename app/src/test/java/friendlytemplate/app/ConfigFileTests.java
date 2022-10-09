package friendlytemplate.app;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigFileTests {
    @Test
    void testParseConfigVersion() {
        InputStream initialStream = new ByteArrayInputStream("template_version: 2".getBytes());
        ConfigFile cfg = new ConfigFile(initialStream);
        assertEquals(2, cfg.getTemplateVersion());
    }

    @Test
    void testParseSampleConfigFile() {
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml");
        ConfigFile cfg = new ConfigFile(sample_file);
        assertEquals(1, cfg.getTemplateVersion());
    }
}
