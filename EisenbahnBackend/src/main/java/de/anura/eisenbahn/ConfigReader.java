package de.anura.eisenbahn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigReader {

    private static final String FILE = "config.cfg";

    private static Map<String, String> config;

    public static void init() {
        File f = new File(FILE);

        if (!f.exists()) {
            try {
                Files.write(f.toPath(), Arrays.asList("user:<none>", "oauth:<token>", "scriptFolder:<folder>"), StandardOpenOption.CREATE);
            } catch (IOException ex) {
                Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, "Failed to create default config", ex);
                return;
            }
        }

        try {
            config = Files.lines(f.toPath()).map(line -> line.trim()).map(line -> line.split(":")).filter(line -> line.length > 1)
                    .collect(Collectors.toMap(line -> line[0], line -> line[1]));
        } catch (IOException ex) {
            Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, "Failed to read config", ex);
        }
    }

    public static String getUser() {
        return config == null ? "" : config.get("user");
    }

    public static String getOauth() {
        return config == null ? "" : config.get("oauth");
    }

    public static String getScriptFolder() {
        return config == null ? "" : config.get("scriptFolder");
    }
}
