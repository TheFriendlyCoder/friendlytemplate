package friendlytemplate.app

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("Tests parsing logic of application config file")
@Subject(ConfigFile)
class ConfigFileSpec extends Specification {
    def "Read sample template from disk"() {
        when: "Parsing a config file with standard content"
        File sourceFile = TestUtils.getFile("simpleExample/friendly.template.yml")
        ConfigFile cfg = ConfigFile.fromYaml(sourceFile)

        then: "reading of standard properties should work through public API"
        with (cfg) {
            templateVersion == 1
            templateDir == sourceFile.parentFile.toPath()
        }
    }

    def "Parse custom options from config file"() {
        when: "Parsing a config file containing custom options just for a specific project"
        File sourceFile = TestUtils.getFile("simpleExample/friendly.template.yml")
        ConfigFile cfg = ConfigFile.fromYaml(sourceFile)

        then: "Custom fields should all be accessible through the public API"
        with(cfg) {
            fieldNames.size() == 1
            fieldNames[0] == "package_name"
            sourceFiles.size() == 2
            sourceFiles[0].toString() == "project.prop"
        }
    }
}
