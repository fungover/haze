package org.fungover.haze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.Files.createDirectory;


public class SaveFile {
    private static Path saveFolder;

    private static void createFolder() {
        String homeFolder = System.getProperty("user.home");
        Path f = Path.of(homeFolder, "fungover");
        Path h = Path.of(homeFolder, "fungover\\haze");
        try {
            createDirectory(f);
            createDirectory(h);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveFolder = h;
    }

    private static Path getPath() {
        final String dateAndTimes = "yyyy-MM-dd-HH-mm-ss";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(dateAndTimes);
        String NameFile = "Data" + "-" + format.format(date);
        String FormatFile = ".txt";
        if (saveFolder == null)
            createFolder();
        return Path.of(saveFolder.toString(), NameFile + FormatFile);
    }


    public static void createFile() {
        Path filePath = getPath();
        try {
            if (!Files.exists(filePath)) {
                System.out.println("The file is now Created.");
                Files.createFile(filePath);
            } else if (Files.exists(filePath)) {
                System.out.println("The file exists.");
            }
        } catch (IOException e) {
            System.out.println("Restart 2.0");
        }
    }

    public static String writeOnFile(Map<String, String> keyValues) {
        String onSuccess = "+OK\r\n";
        String onError = "-Error 'message'\r\n";
        createFile();
        String convertMapToString = keyValues.entrySet().stream().map(e -> e.getKey() + "\n" + e.getValue() + "\n").collect(Collectors.joining());
        System.out.println(convertMapToString);
        try {
            Files.writeString(getPath(), convertMapToString, StandardOpenOption.APPEND);
            System.out.println(onSuccess);
            return onSuccess;
        } catch (IOException e) {
            System.out.println(onError);
            return onError;
        }
    }

    public static void setDir(Path tempDir) {
        saveFolder = tempDir;
    }
}
