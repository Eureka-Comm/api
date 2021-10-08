package com.castellanos94.fuzzylogic.api.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static final String DIRECTORY = "workspace";
    public static final String DATASET_CSV = "dataset.csv";
    public static final String OUTPUT_CSV = "result.csv";

    public static File GET_DATASET_FILE(String id) {
        return new File(DIRECTORY + File.separator + id + File.separator + DATASET_CSV);
    }

    public static boolean SAVE_DATASET(String id, InputStream inputStream) {
        Path path = null;
        try {
            File file = GET_DATASET_FILE(id);
            file.getParentFile().mkdirs();
            path = Files.write(file.toPath(), inputStream.readAllBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOGGER.error("Error al leer los bytes", e);
        }
        return path != null && path.toFile().exists();
    }

    public static boolean DELETE_DATASET(String id) {
        File file = GET_DATASET_FILE(id);
        return file.delete();
    }

    public static Table LOAD_DATASET(String id) throws IOException {
        File file = GET_DATASET_FILE(id);
        if (file.exists()) {
            try {
                return Table.read().csv(file);

            } catch (IOException e) {
                LOGGER.error("Error al leer el dataset csv", e);
                throw e;
            }
        }
        throw new FileNotFoundException("Dataset not found");

    }

    public static File GET_OUTPUT_FILE(String id) {
        return new File(DIRECTORY + File.separator + id + File.separator + OUTPUT_CSV);

    }
}
