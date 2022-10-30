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
            templateDir == sourceFile.parentFile.toPath().toAbsolutePath()
            fieldNames.size() == 1
            fieldNames[0] == "package_name"
            sourceFiles.size() == 2
            sourceFiles[0].toPath().getFileName().toString() == "project.prop"

        }
    }
}
