package dataprocessors;

import org.junit.Test;
import vilij.components.Dialog;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.*;
import static settings.AppPropertyTypes.ERROR_TITLE;
import static settings.AppPropertyTypes.INVALID_ERROR;

public class AppDataTest {

    @Test
    public void savingTextAreaValid() throws IOException {
        String valid = "@a\tlabel1\t3,3";
        Path dataFilePath=new File("fileValid.tsd").toPath();
        PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath));
        writer.write(valid);
        writer.close();
        BufferedReader br = new BufferedReader(new FileReader(dataFilePath.toString()));
        String st = br.readLine();
        assertEquals("@a\tlabel1\t3,3",st);
    }

    @Test(expected = AccessDeniedException.class)
    public void savingTextAreaInvalid() throws IOException {
        String valid = "@a\tlabel1\t3,3";
        Path dataFilePath = Paths.get("");
        PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath));
        writer.write(valid);
    }
}