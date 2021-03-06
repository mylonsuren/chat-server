
import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class Chat {


    private int numParticipants;

    private HashMap<Integer, User> users;

    private clientThread client;

    private String chatName;
    private User newUser;
    private final ChatLog logger = new ChatLog();
    private Utils utils = new Utils();
    private boolean chatNameModified = false;

    public Chat () {
        this.users = new HashMap<>();
        this.numParticipants = 0;
        this.chatName = "";
    }

    private void generateChatName() {
        try {
            chatName = "";
            logger.log("INFO", "Chat.generateChatName", "Creating new chat name", new Utils().getLineNumber());

            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                chatName += users.get(pair.getKey()).getName() + ", ";
            }

            logger.log("SUCCESS", "Chat.generateChatName", "New chat name successfully created", new Utils().getLineNumber());
        } catch (Error error) {
            logger.log("ERROR", "Chat.generateChatName", error.toString(), new Utils().getLineNumber());

        }
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
        this.chatNameModified = true;
        logger.log("SUCCESS", "Chat.setChatName", "Chat name changed to --> " + chatName, new Utils().getLineNumber());

    }

    public String getChatName() {
        return chatName;
    }

    public void resetChatName() {
        generateChatName();
        this.chatNameModified = false;
        logger.log("SUCCESS", "Chat.resetChatName", "Chat name successfully reset", new Utils().getLineNumber());

    }

    public void removeUser(int id) {
        users.remove(id);
        numParticipants--;
        logger.log("SUCCESS", "Chat.removeUser", "User " + id + " successfully removed", new Utils().getLineNumber());
    }

    public void addUser(String name, clientThread client) {
        this.client = client;
        User newUser = new User(name);
        users.put(newUser.getId(), newUser);
        client.setIdNumber(newUser.getId());
        numParticipants++;
        logger.log("SUCCESS", "Chat.addUser", "New user |ID=" + newUser.getId() + "| successfully joined", new Utils().getLineNumber());
    }

    public void setChatNameModified(boolean chatNameModified) {
        this.chatNameModified = chatNameModified;
    }

    public boolean isChatNameModified() {
        return chatNameModified;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public User getUser(int value) {
        if (users.containsKey(value)) {
            return users.get(value);
        }
        return null;
    }

    public int getNumParticipants() {
        return numParticipants;
    }
}
