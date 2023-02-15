package org.fungover.haze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.Files.createDirectory;


public class SaveFile {
    private static Path saveFolder;
    private static final Logger logger = LogManager.getLogger(SaveFile.class);

    private SaveFile() {
    }

    private static void createFolder() {
        String homeFolder = System.getProperty("user.home");
        Path f = Path.of(homeFolder, "fungover");
        Path h = Path.of(homeFolder, "fungover\\haze");
        try {
            createDirectory(f);
            createDirectory(h);
            saveFolder = h;
        } catch (IOException e) {
            logger.error(MessageFormat.format("Failed creating save folder:{0}", e.getMessage()));
        }
    }

    private static Path getPath() {
        final String dateAndTimes = "yyyy-MM-dd-HH-mm-ss";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(dateAndTimes);
        String fileName = "Data" + "-" + format.format(date);
        String fileFormat = ".txt";
        if (saveFolder == null)
            createFolder();
        return Path.of(saveFolder.toString(), fileName + fileFormat);
    }


    public static void createFile() {
        Path filePath = getPath();
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                logger.debug("Created new file");
            } else if (Files.exists(filePath)) {
                logger.debug("File already exists");
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Error creating save file: {0}", filePath));
        }
    }

    public static String writeOnFile(Map<String, String> keyValues) {
        String onSuccess = "+OK\r\n";
        String onError = "-Error 'message'\r\n";
        createFile();
        String convertMapToString = keyValues.entrySet().stream().map(e -> e.getKey() + "\n" + e.getValue() + "\n").collect(Collectors.joining());
        try {
            Files.writeString(getPath(), convertMapToString, StandardOpenOption.APPEND);
            logger.debug("Saved database to file");
            return onSuccess;
        } catch (IOException e) {
            logger.error(MessageFormat.format("Failed to save database to file: {0}", e.getMessage()));
            return onError;
        }
    }

    public static void setDir(Path tempDir) {
        saveFolder = tempDir;
    }
}
