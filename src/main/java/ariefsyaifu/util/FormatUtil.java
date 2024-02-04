package ariefsyaifu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    private FormatUtil() {
    }

    public static boolean isSearchInput(String str, boolean skipNullValueOrEmptyString) {
        if (str == null || str.isEmpty())
            return skipNullValueOrEmptyString;
        String regex = "[a-zA-Z\\d\\s,./\\-+_@%'\":?!&]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
