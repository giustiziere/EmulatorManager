package manager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static List<String> regexMatcher(String template, String text, int groupNum) {
        var list = new ArrayList<String>();
        Pattern p = Pattern.compile(template);
        Matcher m = p.matcher(text);
        while (m.find())
            list.add(m.group(groupNum));
        return list;
    }
}
