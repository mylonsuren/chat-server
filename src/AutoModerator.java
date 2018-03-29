import java.util.HashMap;

public class AutoModerator {

    private HashMap<String, String> actions;
    private HashMap<Integer, String> words;
    private ChatActions chatActions;
    private Chat chat;

    private clientThread client;
    private String message;
    private int maxLevel = 2;

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

        for (int i = 0; i < words.size(); i++) {
            if (this.message.contains(words.get(i))) {
                chat.getUser(client.getIdNumber()).addWarning();
                checkWarningLevel();
            }
        }

    }

    private void checkWarningLevel() {
        int level = chat.getUser(client.getIdNumber()).getWarn();
        if (level >= maxLevel) {
            assertBan();
        } else {
            issueWarning(level);
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
