package friendlytemplate.app

import java.nio.file.Paths
import java.nio.file.Path

class TestUtils {
    private static final Path RESOURCE_PATH = Paths.get("src", "test", "resources")

    static File getFile(String resourcePath) {
        return RESOURCE_PATH.resolve(resourcePath).toFile()
    }
    static Path getPath(String resourcePath) {
        return RESOURCE_PATH.resolve(resourcePath)
    }
}
