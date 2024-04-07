package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigMapper {
    private static final Logger logger = LoggerFactory.getLogger(ConfigMapper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * @param filePath - path to config file
     * @return Map of item and suppliers
     */

    public static <T> T readJsonFromFile(String filePath, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(new File(filePath), typeReference);
        } catch (IOException e) {
            logger.error("Error reading JSON from file: {}", e.getMessage());
            return null;
        }
    }
}
