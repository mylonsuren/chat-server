
import java.util.concurrent.ThreadLocalRandom;

public class User {


    private int id;
    private String name;
    private String timeJoined;
    private ChatLog logger;
    private int warn;
    private boolean timeActive = false;

    public User(String name) {
        this.logger = new ChatLog();
        this.name = name;
        this.timeJoined = new Utils().getTime("FULL_DATE");
        this.id = generateID();
        this.warn = 0;
    }

    public void addWarning() {
        warn++;
    }

    public void setTimeActive() {
        this.timeActive = !timeActive;
    }

    public boolean isTimeActive() {
        return timeActive;
    }

    public int getWarn() {
        return warn;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimeJoined() {
        return timeJoined;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeJoined(String timeJoined) {
        this.timeJoined = timeJoined;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int generateID() {
        logger.log("SUCCESS", "User.generateID", "ID successfully generated");
        return ThreadLocalRandom.current().nextInt(100, 999 + 1);
    }



}
