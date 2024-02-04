package ariefsyaifu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {
    private DateUtil() {
    }

    public static LocalDateTime toLocalDateTime(String dateFrom, String pattern) throws ParseException {
        try {
            SimpleDateFormat genericFormat = new SimpleDateFormat(pattern);
            return genericFormat.parse(dateFrom).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            SimpleDateFormat genericFormat = new SimpleDateFormat(pattern);
            return genericFormat.parse(dateFrom.replace("T", " ")).toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("GMT+7"));
    }

}
