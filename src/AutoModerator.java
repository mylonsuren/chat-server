import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AutoModerator {

    private HashMap<String, String> actions;
    private HashMap<Integer, ReplaceWords> words;
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

        this.words.put(0, new ReplaceWords("test", "****"));
        this.words.put(1, new ReplaceWords("word", "****"));
    }

    public void checkMessage(String message, clientThread client) {
        this.message = message;
        this.client = client;
        logger.log("INFO", "AutoModerator.checkMessage", "Checking message...");
        for (int i = 0; i < words.size(); i++) {
            if (this.message.contains(words.get(i).getWord())) {
                logger.log("INFO", "AutoModerator.checkMessage", "Invalid phrase/word found");
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
            logger.log("INFO", "AutoModerator.checkWarningLevel", "after timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
        } else {
            logger.log("INFO", "AutoModerator.checkWarningLevel", "Issuing warning...");
            issueWarning(level);
        }
    }

    private void issueTimeBan() {
        try {
            client.getOs().println("You have been issued a time ban of " + timeBan + " minutes.");
            logger.log("INFO", "AutoModerator.issueTimeBan", "before timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
            chat.getUser(client.getIdNumber()).setTimeActive();
            logger.log("INFO", "AutoModerator.issueTimeBan", "during timeout --> " + chat.getUser(client.getIdNumber()).isTimeActive());
            TimeUnit.SECONDS.sleep(timeBan);
        } catch (InterruptedException error) {
            logger.log("ERROR", "AutoModerator.issueTimeBan", error.toString());
        }


    }

    private void assertBan() {
        client.getOs().println("You have been banned from the chat.");
        logger.log("INFO", "AutoModerator.assertBan", "User banned");
        chat.removeUser(client.getIdNumber());
        client.getOs().close();
    }

    private void issueWarning(int level) {
        logger.log("INFO", "AutoModerator.issueWarning", "Warning issued to user");
        int diff = maxLevel - level;
        client.getOs().println("This is warning " + level + ". You have " + diff + " warnings left.");
    }


    public String censor(String text, clientThread client) {
        for (int i = 0; i < words.size(); i++) {
            if (text.contains(words.get(i).getWord())) {
                text = text.replace(words.get(i).getWord(), words.get(i).getReplacement());
            }
        }

        return text;
    }

}

class ReplaceWords {

    private String word;
    private String replacement;

    public ReplaceWords(String word, String replacement) {
        this.word = word;
        this.replacement = replacement;
    }

    public String getReplacement() {
        return replacement;
    }

    public String getWord() {
        return word;
    }
}
