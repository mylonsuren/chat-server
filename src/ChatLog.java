

import java.util.HashMap;


interface LogFns {
    void execute();
}

class error extends LogComponent implements LogFns {
    public error(String title, String log, int line) {
        super(title, log, line);
    }
    @Override
    public void execute() {
        System.err.println("ERROR | " + time + " | " + title + " (ln. " + line + ") | " + log);
    }
}

class success extends LogComponent implements LogFns {
    public success(String title, String log, int line) {
        super(title, log, line);
    }
    @Override
    public void execute() {
        System.out.println("SUCCESS | " + time + " | " + title + " (ln. " + line + ") | " + log);
    }
}

class info extends LogComponent implements LogFns {
    public info(String title, String log, int line) {
        super(title, log, line);
    }

    @Override
    public void execute() {
        System.out.println("INFO | " + time + " | " + title + " (ln. " + line + ") | " + log);
    }
}

//class test extends LogComponent implements LogFns {
//    public test(String title, String log) {
//        super(title, log);
//    }
//
//    @Override
//    public void execute() { System.out.println("TEST | " + time + " | " + title + " | " + log ); }
//
//}
//
//class debug extends LogComponent implements LogFns {
//    public debug(String title, String log) {
//        super(title, log);
//    }
//
//    @Override
//    public void execute() { System.out.println("DEBUG | " + time + " | " + title + " | " + log ); }
//
//}


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


    public void log(String type, String title, String log, int line) {

        logType.put("ERROR", new error(title, log, line));
        logType.put("SUCCESS", new success(title, log, line));
        logType.put("INFO", new info(title, log, line));
//        logType.put("TEST", new test(title, log));
//        logType.put("DEBUG", new debug(title, log));

        this.message = new LogComponent(title, log, line);

        String time = utils.getTime("FULL_DATE");

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
    int line;

    public LogComponent(String title, String log, int line) {
        this.log = log;
        this.title = title;
        this.time = utils.getTime("FULL_DATE");
        this.line = line;
    }

}

