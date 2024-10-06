// ConfigLoader.java
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.InputStream;
import libs.*;

public class ConfigLoader {
    public static Config loadConfig(String filePath) {
        // Initialize LoaderOptions (configure as needed)
        LoaderOptions options = new LoaderOptions();
        // For example, you can set specific options here
        // options.setAllowDuplicateKeys(false);

        // Create a Constructor with the root class and LoaderOptions
        Constructor constructor = new Constructor(Config.class, options);

        // Create Yaml instance with the custom Constructor
        Yaml yaml = new Yaml(constructor);

        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            // Load and return the Config object
            return yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
}
