package org.fungover.haze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveFile {
    public static void main(String[] args) {
        createFile();
    }
    public static void createFile() {
        String FormatFile = ".txt";
        String NameFile = "test";
        String homeFolder = System.getProperty("user.home");
        Path filePath = Path.of(homeFolder,"IdeaProjects","haze",NameFile+FormatFile);

        try {
            System.out.println(Files.exists(filePath));
            if (!Files.exists(filePath))
            {
            System.out.println("The file is now Created.");
            Files.createFile(filePath);
            }else if (Files.exists(filePath)){
                System.out.println("The file exists.");
            }


        }catch (IOException e){
            System.out.println("restart");
        }

    }

}
