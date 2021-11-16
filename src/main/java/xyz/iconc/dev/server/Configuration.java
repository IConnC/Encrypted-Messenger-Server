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
        return properties.get(cfg.toString());
    }

    /**
     * Loads the file into the properties variable
     */
    private void loadFile() {
        try {
            FileInputStream inputStream = new FileInputStream(PROPERTY_FILE_LOCATION);
            properties.load(inputStream);
            inputStream.close();
            validateFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Sets the default values of configuration options from configOptionsMap to the
     *  properties variable.
     */
    private void generateFile() {
        properties.clear();
        for (ConfigOptions _configOption : configOptionsMap.keySet()){
            properties.put(_configOption.toString(), configOptionsMap.get(_configOption));
        }
        savePropertiesToDisk();
    }

    /**
     * Saves the contents of properties to disk.
     */
    private void savePropertiesToDisk() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PROPERTY_FILE_LOCATION);
            properties.store(fileOutputStream, "");
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * Adds default values not present in the current server.properties file into the file.
     */
    private void validateFile() {
        boolean changedValues = false;
        for (ConfigOptions configOption : ConfigOptions.values()) {
            if (getConfigValue(configOption) == null) {
                changedValues = true;
                properties.put(configOption.toString(), configOption.getValue());
            }
        }
        if (changedValues) savePropertiesToDisk();
    }


    /**
     * Loads the default options specified in the ConfigOptions enum and places it in the
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
        DATABASE_CONNECTION_STRING("jdbc:mysql://[host][,failoverhost][:port]/[database]"),
        DATABASE_USER("user"),
        DATABASE_PASSWORD("password");
        private final String defaultValue;

        public String getValue() {
            return this.defaultValue;
        }


        private ConfigOptions(String value) {
            defaultValue = value;
        }
    }
}

