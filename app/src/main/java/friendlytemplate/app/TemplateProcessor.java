package friendlytemplate.app;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TemplateProcessor {
    private final Logger logger = LoggerFactory.getLogger(TemplateProcessor.class);
    private final ConfigFile configFile;
    private final Map<String, Object> parameters;
    private final Path outputFolder;

    public TemplateProcessor(Path outdir, ConfigFile cfg, Map<String, Object> params) {
        configFile = cfg;
        parameters = params;
        outputFolder = outdir;
    }

    public List<String> validate() {
        List<String> retval = new LinkedList<>();
        File[] children = outputFolder.toFile().listFiles();
        if (outputFolder.toFile().exists() && children != null && children.length > 0) {
            retval.add("Output folder is non-empty");
        }
        return retval;
    }
    public boolean run() throws IOException {
        if (!outputFolder.toFile().exists()) {
            boolean result = outputFolder.toFile().mkdirs();
            if (!result) {
                logger.error("Unable to create template folder: {}", outputFolder);
                return false;
            }
        }
        PebbleEngine engine = new PebbleEngine.Builder().build();

        for (File srcFile: configFile.getSourceFiles()) {
            if (!srcFile.exists()) {
                throw new FileNotFoundException("Template file " + srcFile + " not found");
            }

            Path relativePath = Paths.get(configFile.getTemplateDir().toUri().relativize(srcFile.toURI()).getPath());
            PebbleTemplate pathTemplate = engine.getLiteralTemplate(relativePath.toString());
            Writer pathWriter = new StringWriter();
            pathTemplate.evaluate(pathWriter, parameters);
            relativePath = Paths.get(pathWriter.toString());

            PebbleTemplate fileTemplate = engine.getTemplate(srcFile.toString());
            Writer outputFileContents = new StringWriter();
            fileTemplate.evaluate(outputFileContents, parameters);

            File targetFile = outputFolder.resolve(relativePath).toFile();
            if (!targetFile.getParentFile().exists()) {
                boolean result = targetFile.getParentFile().mkdirs();
                if (!result) {
                    throw new IOException("Unable to create output folder " + targetFile.getParentFile().toString());
                }
            }
            try(FileWriter outputFileWriter = new FileWriter(targetFile)) {
                outputFileWriter.write(outputFileContents.toString());
            }
        }
        return true;
    }
}
