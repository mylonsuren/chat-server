import com.oracle.tools.packager.Log;

import java.util.HashMap;


interface LogFns {
    void execute();
}

class error extends LogComponent implements LogFns {
    public error(String time, String title, String log) {
        super(time,title, log);
    }
    @Override
    public void execute() {
        System.out.println("ERROR LOGGING");
        System.out.println(time  + ": " + title + " -- " + log);
    }
}

class success extends LogComponent implements LogFns {
    public success(String time, String title, String log) {
        super(time, title, log);
    }
    @Override
    public void execute() {
        System.out.println("SUCCESS LOGGING");
        System.out.println(time + ": " + title + " -- " + log);
    }
}


public class ChatLog {

    private HashMap<String, LogComponent> logs;

    private HashMap<String, LogFns> logType;

    private LogComponent message;

    private String type;

    public ChatLog() {
        logs = new HashMap<>();
        logType = new HashMap<>();


    }

    public HashMap<String, LogFns> getLogType() {
        return logType;
    }


    public void log(String type, String time, String title, String log) {

        logType.put("ERROR", new error(time, title, log));
        logType.put("SUCCESS", new success(time, title, log));

        this.message = new LogComponent(title, log);

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
    String title;
    String log;
    String time;

    public LogComponent(String title, String log) {
        this.log = log;
        this.title = title;
    }

    public LogComponent(String time, String title, String log) {
        this.log = log;
        this.title = title;
        this.time = time;
    }
}

