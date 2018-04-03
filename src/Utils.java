import java.text.SimpleDateFormat;
import java.util.HashMap;


public class Utils {

    private ChatLog logger;

    private HashMap<String, String> timeFormat;


    public Utils() {
        this.timeFormat = new HashMap<>();
        this.timeFormat.put("SHORT_DATE", "HH:mm:ss z");
        this.timeFormat.put("FULL_DATE", "yyyy-MM-dd HH:mm:ss z");
    }

    public String getTime(String format) {
        String msgTime = new SimpleDateFormat(timeFormat.get(format)).format(new java.util.Date());
        return msgTime;
    }

    public void printToServer(String message, clientThread[] clients) {
        logger = new ChatLog();
        logger.log("INFO", "Utils.printToServer", "PRINT TO SERVER --> INITIATED");
        for (clientThread client : clients) {
            if (client != null) {
                client.getOs().println(message);
            }
        }
    }

}
