package friendlytemplate.utilities

import spock.lang.Specification

class StringUtilsSpec extends Specification {
    def "Sample test"() {
        expect:
        StringUtils.getMessage() == "Hello World"
    }
}