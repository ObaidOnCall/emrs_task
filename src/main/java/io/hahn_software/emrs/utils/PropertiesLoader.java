package io.hahn_software.emrs.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Builder;


@Builder
public class PropertiesLoader {


    public Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace(); // Handle the exception appropriately
        }
        return properties;
    }
}