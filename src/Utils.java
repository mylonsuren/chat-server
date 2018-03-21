import java.text.SimpleDateFormat;
import java.util.HashMap;


public class Utils {

    private HashMap<String, String> timeFormat;


    public Utils() {
        this.timeFormat = new HashMap<>();
        this.timeFormat.put("hours:minutes", "HH:mm z");
    }

    public String getTime(String format) {
        String msgTime = new SimpleDateFormat(timeFormat.get(format)).format(new java.util.Date());
        return msgTime;
    }

}
