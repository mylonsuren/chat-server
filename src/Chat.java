
import java.util.*;

public class Chat {


    private int numParticipants;

    private HashMap<Integer, User> users;

    private clientThread client;

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
        logger.log("SUCCESS", "Chat.setChatName", "Chat name changed to --> " + chatName);
    }

    public String getChatName() {
        return chatName;
    }

    public void resetChatName() {
        generateChatName();
        this.chatNameModified = false;
        logger.log("SUCCESS", "Chat.resetChatName", "Chat name successfully reset");
    }

    public void removeUser(int id) {
        users.remove(id);
        numParticipants--;
        logger.log("SUCCESS", "Chat.removeUser", "User " + id + " successfully removed");
    }

    public void addUser(String name, clientThread client) {
        this.client = client;
        User newUser = new User(name);
        users.put(newUser.getId(), newUser);
        client.setIdNumber(newUser.getId());
        numParticipants++;
        logger.log("SUCCESS", "Chat.addUser", "New user " + newUser.getId() + " successfully joined");
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
