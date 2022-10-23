package friendlytemplate.app

import spock.lang.Specification


class ConfigFileTests extends Specification {
    def "Read template version from config file"() {
        when:
        InputStream initialStream = new ByteArrayInputStream("templateVersion: 2".bytes)
        ConfigFile cfg = ConfigFile.fromYaml(initialStream)

        then:
        cfg.getTemplateVersion() == 2
    }

    def "Read sample template from disk"() {
        when:
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml")
        ConfigFile cfg = ConfigFile.fromYaml(sample_file)

        then:
        cfg.getTemplateVersion() == 1
    }

    def "Read field names from config file"() {
        when:
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml")
        ConfigFile cfg = ConfigFile.fromYaml(sample_file)
        List<String> values = cfg.getFieldNames()

        then:
        values.size() == 1
        values[0] == "package_name"
    }
}
