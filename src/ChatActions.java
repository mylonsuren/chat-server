
import java.util.HashMap;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class ChatActions {

    private String action;
    private Chat chat;
    private clientThread client;
    private String actionMessage;

    private HashMap<String,String> commands = new HashMap<>();

    public ChatActions() {
        this.chat = new Chat();
        initiateCommands();
    }

    public Chat getChat() {
        return chat;
    }

    public void handleAction(String message, clientThread client) {
        this.client = client;
        this.action = message;
        if (action.startsWith("/chat-name")) {
            parseActionMessage();
            System.out.println("parseActionMessage --> DONE");
            chatName();
            System.out.println("chatName --> DONE");
        } else if (action.startsWith("/users")) {
            parseActionMessage();
            userManagement();
        } else if (action.startsWith("/quit")) {
            System.out.println("quit --> DONE");
            quitChat();
        } else if (action.startsWith("/shutdown")) {
            serverShutdown();
            System.out.println("serverSHUTDOWN --> DONE");
        } else {
            System.out.println("INVALID ACTION");
            return;
        }
    }

    private void parseActionMessage() {
        String[] message = action.split("\\s", 2);
        System.out.println("ChatActions.parseActionMessage");
        System.out.println("ACTION = " + message[0]);
        if (message.length > 1 && message[1] != null) {
            System.out.println("ACTION PARAMETER = " + message[1]);
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    private void printToClient(String message) {
        System.out.println("ChatActions.printToClient");
        client.getOs().println(message);
        System.out.println("PRINT TO CLIENT --> DONE");
    }

    private void initiateCommands() {
        commands.put("VIEW_CHAT_NAME", "/chat-name-view");
        commands.put("REMOVE_CHAT_NAME", "/chat-name-remove");
        commands.put("SET_CHAT_NAME", "/chat-name-set");

        commands.put("REMOVE_USER", "/users-remove");
        commands.put("VIEW_USERS", "/users-view");

        commands.put("LEAVE_CHAT", "/quit");

        commands.put("SHUT_DOWN", "/shutdown");
    }

    private void serverShutdown() {

        try {
            clientThread[] threads = client.getThreads();

            System.out.println(client.getMsgName() + " has shut down server");
            System.out.println("Shutting down the server...");
            for (int i = 0; i < chat.getNumParticipants(); i++) {
                threads[i].getOs().println("Shutting down server...");
                threads[i].getOs().println(client.getMsgName() + " has shut down server");

                threads[i].getIs().close();
                threads[i].getOs().close();
                threads[i].getClientSocket().close();

            }

            System.out.println("Server shutdown initiated...");
            System.out.println("Server shutting down...");
            System.exit(0);

        } catch (IOException error) {
            System.out.println("Shutdown server failed.");
            System.out.println(error);
        }
    }

    private void quitChat() {
        clientThread[] threads = client.getThreads();
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null && threads[i] != client
                    && threads[i].getClientName() != null) {
                threads[i].getOs().println("*** The user " + client.getMsgName()
                        + " is leaving the chat room ***");
            }

            if (threads[i] == client) {
                threads[i] = null;
            }
        }

        chat.removeUser(client.getIdNumber());

        printToClient("*** Goodbye " + client.getMsgName() + " ***");
        try {
            client.getIs().close();
            client.getOs().close();
            client.getClientSocket().close();

        } catch (IOException error) {
            System.out.println(error);
        }

    }

    private void chatName() {
        System.out.println("ChatActions.chatName");
        System.out.println("ACTION: " + action);
        if (action.equals(commands.get("VIEW_CHAT_NAME"))) {
            System.out.println("VIEW_CHAT_NAME");
            String chatName = chat.getChatName();
            System.out.println(chatName);
            printToClient(chatName);
        } else if (action.equals(commands.get("REMOVE_CHAT_NAME"))) {
            System.out.println("REMOVE_CHAT_NAME");
            chat.resetChatName();
        } else if (action.equals(commands.get("SET_CHAT_NAME"))) {
            System.out.println("SET_CHAT_NAME: " + actionMessage);
            chat.setChatName(actionMessage);
        } else {
            System.out.println("ChatActions.chatName --> NO RESPONSE");
        }
    }

    private void userManagement() {
        if (action.equals(commands.get("REMOVE_USER"))) {
            System.out.println("REMOVE_USER");
            removeUser(client.getThreads(), actionMessage);
        } else if (action.equals(commands.get("VIEW_USERS"))) {
            System.out.println("VIEW_USERS");
            viewUsers();
        }
    }

    private void viewUsers() {
        try {
            System.out.println("ChatActions.viewUsers");
            printToClient("CURRENT PARTICIPANTS: ");
            Iterator it = chat.getUsers().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                printToClient(pair.getKey() + " - " + chat.getUsers().get(pair.getKey()).getName());
            }
        } catch (Error error) {
            System.out.println(error);
        }
    }

    private void removeUser(clientThread[] threads, String user) {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                threads[i].getOs().println(threads[i].getMsgName() + " removed " + user + " from the chat");
            }

            if (threads[i] != null && threads[i].getClientName()!= null
                    && threads[i].getMsgName().equals(user)) {
                threads[i].getOs().println("You have been removed from the chat by " + client.getMsgName());
                threads[i].getOs().close();
                chat.removeUser(i);
                break;
            }
        }
    }


    public void addUser(String name) {
        System.out.println("ChatActions.addUser");
        System.out.println("USER: " + name);

        System.out.println("USERS: " + chat.getUsers());

        chat.addUser(name);
        if (!chat.isChatNameModified()) {
            chat.resetChatName();
        }
        System.out.println("USER ADDED --> DONE");
    }



}
