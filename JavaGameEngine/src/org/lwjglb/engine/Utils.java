package org.lwjglb.engine;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
    	Scanner scanner = new Scanner(new File(fileName), java.nio.charset.StandardCharsets.UTF_8.name());
        result = scanner.useDelimiter("\\A").next();
        return result;
    }

}