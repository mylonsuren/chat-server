import java.text.SimpleDateFormat;
import java.util.HashMap;


@SuppressWarnings("ALL")
public class Utils {

    private ChatLog logger;

    private final HashMap<String, String> timeFormat;


    public Utils() {
        this.timeFormat = new HashMap<>();
        this.timeFormat.put("SHORT_DATE", "HH:mm:ss z");
        this.timeFormat.put("FULL_DATE", "yyyy-MM-dd HH:mm:ss z");
    }

    public String getTime(String format) {
        return new SimpleDateFormat(timeFormat.get(format)).format(new java.util.Date());
    }

    public void printToServer(String message, clientThread[] clients) {
        logger = new ChatLog();
        logger.log("INFO", "Utils.printToServer", "PRINT TO SERVER --> INITIATED", getLineNumber());
        for (clientThread client : clients) {
            if (client != null) {
                client.getOs().println(message);
            }
        }
    }

    public int getLineNumber() {
        return ___8drrd3148796d_Xaf();
    }

    /** This methods name is ridiculous on purpose to prevent any other method
     * names in the stack trace from potentially matching this one.
     *
     * @return The line number of the code that called the method that called
     *         this method(Should only be called by getLineNumber()).
    */
    @SuppressWarnings("SpellCheckingInspection")
    private int ___8drrd3148796d_Xaf() {
        boolean thisOne = false;
        int thisOneCountDown = 1;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for(StackTraceElement element : elements) {
            String methodName = element.getMethodName();
            int lineNum = element.getLineNumber();
            if(thisOne && (thisOneCountDown == 0)) {
                return lineNum;
            } else if(thisOne) {
                thisOneCountDown--;
            }
            //noinspection SpellCheckingInspection,SpellCheckingInspection
            if(methodName.equals("___8drrd3148796d_Xaf")) {
                thisOne = true;
            }
        }
        return -1;
    }

}
