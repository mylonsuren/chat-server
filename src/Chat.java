
import java.util.*;

public class Chat {


    private int numParticipants;

    private HashMap<Integer, User> users;

    private String chatName;
    private User newUser;
    private ChatLog logger = new ChatLog();
    private boolean chatNameModified = false;

    public Chat () {
        this.users = new HashMap<Integer, User>();
        this.numParticipants = 0;
        this.chatName = "";
    }

    private void generateChatName() {
        try {
            chatName = "";
            logger.log("INFO", "Chat.generateChatName", "Creating new chat name");

            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                chatName += users.get(pair.getKey()).getName() + ", ";
            }

            logger.log("SUCCESS", "Chat.generateChatName", "New chat name successfully created");
        } catch (Error error) {
            logger.log("ERROR", "Chat.generateChatName", error.toString());

        }
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
        this.chatNameModified = true;
    }


    public String getChatName() {
        return chatName;
    }

    public void resetChatName() {
        generateChatName();
        this.chatNameModified = false;
    }

    public void removeUser(int id) {
        users.remove(id);
        numParticipants--;
    }

    public void addUser(String name) {
        User newUser = new User(name);
        users.put(newUser.getId(), newUser);
        numParticipants++;
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

    public int getNumParticipants() {
        return numParticipants;
    }
}
