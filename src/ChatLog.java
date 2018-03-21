import com.oracle.tools.packager.Log;

import java.util.HashMap;


interface LogFns {
    void execute();
}

class error extends LogComponent implements LogFns {
    public error(String title, String log) {
        super(title, log);
    }
    @Override
    public void execute() {
        System.err.println("ERROR | " + time + " | " + title + " | " + log);
    }
}

class success extends LogComponent implements LogFns {
    public success(String title, String log) {
        super(title, log);
    }
    @Override
    public void execute() {
        System.out.println("SUCCESS | " + time + " | " + title + " | " + log);
    }
}

class info extends LogComponent implements LogFns {
    public info(String title, String log) {
        super(title, log);
    }

    @Override
    public void execute() {
        System.out.println("INFO | " + time + " | " + title + " | " + log);
    }
}


public class ChatLog {

    public Utils utils;

    private HashMap<String, LogComponent> logs;

    private HashMap<String, LogFns> logType;

    private LogComponent message;

    private String type;

    public ChatLog() {
        this.utils = new Utils();
        logs = new HashMap<>();
        logType = new HashMap<>();
    }

    public HashMap<String, LogFns> getLogType() {
        return logType;
    }


    public void log(String type, String title, String log) {

        logType.put("ERROR", new error(title, log));
        logType.put("SUCCESS", new success(title, log));
        logType.put("INFO", new info(title, log));

        this.message = new LogComponent(title, log);

        String time = utils.getTime("hours:minutes");

        logs.put(time, message);

        validateLog(type);
    }

    public void validateLog(String type) {
        if (logType.containsKey(type) || logType.containsValue(type)) {
            logType.get(type).execute();
        }
    }

}




class LogComponent {

    private Utils utils = new Utils();

    String title;
    String log;
    String time;

    public LogComponent(String title, String log) {
        this.log = log;
        this.title = title;
        this.time = utils.getTime("hours:minutes");
    }

}

