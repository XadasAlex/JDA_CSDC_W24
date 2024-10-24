package bot.utils;

import java.util.List;

public class ContentCollection {
    public static List<String> commandList;
    public static List<String> emojies;

    public ContentCollection(List<String>... data) {
        List<String> commandList = data[0];
        boolean emojiesExist = false;

        try {
            List<String> emojies = data[1];
            emojiesExist = true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }




    }
}
