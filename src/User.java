import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class User {


    private int id;
    private String name;
    private String timeJoined;


    public User(String name) {
        this.name = name;
        this.timeJoined = new SimpleDateFormat("HH:mm").format(new java.util.Date());;
        this.id = generateID();
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
        return ThreadLocalRandom.current().nextInt(100, 999 + 1);
    }



}
