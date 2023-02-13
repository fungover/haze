package org.fungover.haze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class SaveFile {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        createFile();
        writeOnFile();
    }
    private static Path getPath() {
        String FormatFile = ".txt";
        String NameFile = "test";
        String homeFolder = System.getProperty("user.home");
        Path filePath = Path.of(homeFolder,"IdeaProjects","haze",NameFile+FormatFile);
        return filePath;
    }
    public static void createFile() {
        Path filePath = getPath();
        try {
            System.out.println(Files.exists(filePath));
            if (!Files.exists(filePath)) {
            System.out.println("The file is now Created.");
            Files.createFile(filePath);
            }else if (Files.exists(filePath)){
                System.out.println("The file exists.");
            }
        }catch (IOException e){
            System.out.println("Restart");
        }

    }

    public static void writeOnFile(){
        System.out.print("Key: ");
        final String Key = sc.next();
        System.out.print("Value: ");
        final String Value = sc.next();
        try {
            Files.writeString(getPath(),Key+":"+Value+"\n", StandardOpenOption.APPEND);
        }catch (IOException e){
            System.out.println("Restart");
        }



    }

}
