package xyz.iconc.dev.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private static final String PROPERTY_FILE_LOCATION = "./server.properties";
    private Properties properties;
    private Map<ConfigOptions, String> configOptionsMap;

    public Configuration() {
        properties = new Properties();
        configOptionsMap = new HashMap<>();

        generateDefaultOptions();

        if (!new File(PROPERTY_FILE_LOCATION).exists()) generateFile();
        loadFile();
    }


    public Object getConfigValue(ConfigOptions cfg) {
        return properties.get(cfg);
    }


    private void loadFile() {
        try {
            FileInputStream inputStream = new FileInputStream(PROPERTY_FILE_LOCATION);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void generateFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PROPERTY_FILE_LOCATION);
            properties.clear();
            for (ConfigOptions _configOption : configOptionsMap.keySet()){
                properties.put(_configOption.toString(), configOptionsMap.get(_configOption));
            }
            properties.store(fileOutputStream, "");
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the default options listed in the ConfigOptions enum and places it in the
     *  configOptions map
     */
    private void generateDefaultOptions() {
        for (ConfigOptions _configOptions : ConfigOptions.values()) {
            configOptionsMap.put(_configOptions, _configOptions.getValue());
        }
    }



    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
    }
    enum ConfigOptions {
        DATABASE_CONNECTION_STRING("0");
        private final String defaultValue;

        public String getValue() {
            return this.defaultValue;
        }


        private ConfigOptions(String value) {
            defaultValue = value;
        }
    }
}

