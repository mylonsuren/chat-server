import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AutoModerator {

    private HashMap<String, String> actions;
    private HashMap<Integer, String> words;
    private ChatActions chatActions;
    private Chat chat;
    private ChatLog logger = new ChatLog();

    private clientThread client;
    private String message;
    private int maxLevel = 2;
    private int timeBan = 5;

    public AutoModerator(ChatActions chatActions) {
        this.chatActions = chatActions;
        this.chat = chatActions.getChat();
        this.actions = new HashMap<>();
        this.words = new HashMap<>();

        this.actions.put("REMOVE_USER", "remove");
        this.actions.put("WARN_USER", "warn");
        this.actions.put("TIME_BAN", "time-ban");

        this.words.put(0, "test");
    }

    public void checkMessage(String message, clientThread client) {
        this.message = message;
        this.client = client;
        logger.log("INFO", "AutoModerator.checkMessage", "Checking message...");
        for (int i = 0; i < words.size(); i++) {
            if (this.message.contains(words.get(i))) {
                chat.getUser(client.getIdNumber()).addWarning();
                checkWarningLevel();
            }
        }
    }

    private void checkWarningLevel() {
        int level = chat.getUser(client.getIdNumber()).getWarn();
        if (level > maxLevel) {
            logger.log("INFO", "AutoModerator.checkWarningLevel", "Banning user...");
            assertBan();
        } else if (level == maxLevel) {
            logger.log("INFO", "AutoModerator.checkWarningLevel", "Issuing time ban...");
            issueTimeBan();
            chat.getUser(client.getIdNumber()).setTimeActive();
            System.out.println("after timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
        } else {
            logger.log("INFO", "AutoModerator.checkWarningLevel", "Issuing warning...");
            issueWarning(level);
        }
    }

    private void issueTimeBan() {
        try {
            client.getOs().println("You have been issued a time ban of " + timeBan + " minutes.");
            System.out.println("before timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
            chat.getUser(client.getIdNumber()).setTimeActive();
            System.out.println("during timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
            TimeUnit.MINUTES.sleep(timeBan);
        } catch (InterruptedException error) {

        }


    }

    private void assertBan() {
        client.getOs().println("You have been banned from the chat.");
        chat.removeUser(client.getIdNumber());
        client.getOs().close();
    }

    private void issueWarning(int level) {
        int diff = maxLevel - level;
        client.getOs().println("This is warning " + level + ". You have " + diff + " warnings left.");
    }

}
