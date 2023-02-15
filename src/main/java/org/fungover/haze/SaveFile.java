package org.fungover.haze;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;


public class SaveFile {
    private static void createFolder() {
        String homeFolder = System.getProperty("user.home");
        File f = new File(homeFolder, "fungover");
        File h = new File(homeFolder, "fungover\\haze");
        if (f.mkdir() && h.mkdir()) {
            System.out.println("Directory has been created successfully");
        } else {
            System.out.println("The folder already exists");
        }
    }

    private static Path getPath() {
        final String dateAndTimes = "yyyy-MM-dd-HH-mm-ss";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(dateAndTimes);
        String NameFile = "test" + "-" + format.format(date);
        String FormatFile = ".txt";
        createFolder();
        String homeFolder = System.getProperty("user.home");
        return Path.of(homeFolder, "fungover", "haze", NameFile + FormatFile);
    }


    public static void createFile() {
        Path filePath = getPath();
        try {
            //System.out.println(Files.exists(filePath));
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
        String str = keyValues.entrySet().stream().map(e -> e.getKey() + "\n" + e.getValue() + "\n").collect(Collectors.joining());
        System.out.println(str);

        try {
            Files.writeString(getPath(), str + "\n", StandardOpenOption.APPEND);
            System.out.println(onSuccess);
            return onSuccess;
        } catch (IOException e) {
            System.out.println(onError);
            return onError;
        }
    }
}

