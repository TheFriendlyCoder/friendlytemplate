package friendlytemplate.app

import spock.lang.Specification

import java.nio.file.Path


class ConfigFileSpec extends Specification {
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

    def "Parse common options from config file"() {
        when:
        InputStream sample_file = getClass().getClassLoader().getResourceAsStream("simpleExample/friendly.template.yml")
        ConfigFile cfg = ConfigFile.fromYaml(sample_file)
        List<String> fieldNames = cfg.getFieldNames()
        List<Path> files = cfg.getSourceFiles()

        then:
        fieldNames.size() == 1
        fieldNames[0] == "package_name"
        files.size() == 2
        files[0].toString() == "project.prop"
    }

    def "Instantiate config from file"() {
        when:
        File sourceFile = new File(getClass().getClassLoader().getResource("simpleExample/friendly.template.yml").toURI());
        ConfigFile configFile = ConfigFile.fromYaml(sourceFile)

        then:
        configFile.templateVersion == 1
        configFile.fieldNames.size() == 1
        configFile.fieldNames[0] == "package_name"
        configFile.sourceFiles.size() == 2
        configFile.sourceFiles[0].toString() == "project.prop"
        configFile.sourceFiles[1].toString() == "src.{{ package_name }}/version.txt"
    }
}
