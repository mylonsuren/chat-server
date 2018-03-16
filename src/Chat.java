import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Chat {


    private int numParticipants;
//    private ArrayList<String> users;

    private HashMap<Integer, User> users;

    private String chatName;

    public Chat () {
        this.numParticipants = 0;
        this.users = new HashMap<Integer, User>();
        this.chatName = "";
    }

    private void generateChatName() {
        try {
            Iterator it = users.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
//                if () {
//                    chatName += users.get(pair.getKey()).getName();
//                    it.remove();
//                    break;
//                } else {
                    chatName += users.get(pair.getKey()).getName() + ", ";
//                }
                it.remove();
            }

            System.out.println(chatName);
        } catch (Error error) {
            System.out.println(error);
        }
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void test() {
        System.out.println(chatName);
        System.out.println(users);
        System.out.println(numParticipants);
        System.out.println("TESTING");
    }

    public String getChatName() {
        System.out.println("Chat.java: " + chatName);
        return chatName;
    }

    public void resetChatName() {
        generateChatName();
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




}
