package utils;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;

import javax.management.relation.Role;
import java.io.FileReader;

public class JSONReader {
    public static void readFile(String relativePath, String filename) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(relativePath + "/" + filename)) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
