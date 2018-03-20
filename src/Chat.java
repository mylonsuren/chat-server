import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Chat {


    private int numParticipants;

    private HashMap<Integer, User> users;

    private String chatName;
    private User newUser;
    private boolean chatNameModified = false;

    public Chat () {
        this.users = new HashMap<Integer, User>();
        this.numParticipants = 0;
        this.chatName = "";
    }

    private void generateChatName() {
        try {
            chatName = "";
            System.out.println("Chat.generateChatName");
            System.out.println("USERS: " + users);
            System.out.println("NAME: " + chatName);

            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                chatName += users.get(pair.getKey()).getName() + ", ";
            }

            System.out.println(chatName);
        } catch (Error error) {
            System.out.println(error);
        }
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
        this.chatNameModified = true;
    }


    public String getChatName() {
        System.out.println("Chat.getChatName: " + chatName);
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
