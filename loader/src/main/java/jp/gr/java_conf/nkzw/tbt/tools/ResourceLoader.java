package jp.gr.java_conf.nkzw.tbt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {

    public static InputStream getInputStream(String path) throws IOException {
        var is = ResourceLoader.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path + " not found");
        }
        return is;
    }

    public static BufferedReader getReader(String path) throws IOException {
        var is = getInputStream(path);
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    public static List<String> readAllLines(String path) throws IOException {
        var result = new ArrayList<String>();
        try (var reader = getReader(path)) {
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
            }
        }
        return result;
    }
}
