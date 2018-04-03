
import java.util.HashMap;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class ChatActions {

    private String action;
    private Chat chat;
    private clientThread client;
    private String actionMessage;

    private ChatLog logger;
    private Utils utils;

    private HashMap<String,String> commands = new HashMap<>();

    public ChatActions() {
        this.chat = new Chat();
        this.logger = new ChatLog();
        this.utils = new Utils();
        initiateCommands();
    }

    public Chat getChat() {
        return chat;
    }

    public void handleAction(String message, clientThread client) {
        logger.log("INFO", "ChatActions.handleAction",  "action sent to action handler");
        this.client = client;
        this.action = message;
        if (action.startsWith("/chat-name")) {
            parseActionMessage();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
            chatName();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
        } else if (action.startsWith("/users")) {
            parseActionMessage();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
            userManagement();
        } else if (action.startsWith("/quit")) {
            quitChat();
            logger.log("SUCCESS", "ChatActions.handleAction", client.getIdNumber() + " : QUIT CHAT");
        } else if (action.startsWith("/modify")) {
            parseActionMessage();
            modify();
        } else if (action.startsWith("/shutdown")) {
            serverShutdown();
            logger.log("SUCCESS", "ChatActions.handleAction", "SERVER SHUTDOWN");
        } else {
            printToClient("Invalid action, please enter a valid action or message\n");
            logger.log("ERROR", "ChatActions.handleAction", "INVALID ACTION");
            return;
        }
        logger.log("SUCCESS", "ChatActions.handleAction", "ACTION COMPLETED");
    }

    private void parseActionMessage() {
        actionMessage = null;
        String[] message = action.split("\\s", 2);
        logger.log("INFO", "ChatActions.parseActionMessage", "ACTION = " + message[0]);
        if (message.length > 1 && message[1] != null) {
            logger.log("INFO", "ChatActions.parseActionMessage", "ACTION PARAMETER = " + message[1]);
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    private void printToClient(String message) {
        logger.log("INFO", "ChatActions.printToClient", "PRINT TO CLIENT --> INITIATED");
        client.getOs().print(message);
        logger.log("SUCCESS", "ChatActions.printToClient", "PRINT TO CLIENT --> DONE");
    }



    private void initiateCommands() {
        commands.put("VIEW_CHAT_NAME", "/chat-name-view");
        commands.put("REMOVE_CHAT_NAME", "/chat-name-remove");
        commands.put("SET_CHAT_NAME", "/chat-name-set");

        commands.put("REMOVE_USER", "/users-remove");
        commands.put("VIEW_USERS", "/users-view");

        commands.put("MODIFY_NAME", "/modify-name");

        commands.put("LEAVE_CHAT", "/quit");

        commands.put("SHUT_DOWN", "/shutdown");
    }

    private void modify() {
        if (action.equals(commands.get("MODIFY_NAME"))) {
            logger.log("INFO", "ChatActions.modify", "MODIFY_NAME");
            modifyName();
        }
    }

    private void modifyName() {
        if (actionMessage != null) {
            String currentName = client.getMsgName();
            String newName = actionMessage;
            client.setMsgName(newName);
            chat.getUsers().get(client.getIdNumber()).setName(newName);
            logger.log("INFO", "ChatActions.modifyName", "Current user name: " + currentName + ", new user name: " + newName);
        } else {
            printToClient("Please provide a new name as well. (e.g. /modify-name John)\n");
            logger.log("ERROR", "ChatActions.modifyName","No parameter provided");
        }
    }

    private void serverShutdown() {

        try {
            clientThread[] threads = client.getThreads();

            logger.log("INFO", "ChatActions.serverShutdown",client.getMsgName() + " has shut down server");
            logger.log("INFO", "ChatActions.serverShutdown","Shutting down the server");
            for (int i = 0; i < chat.getNumParticipants(); i++) {
                threads[i].getOs().println("Shutting down server...");
                threads[i].getOs().println(client.getMsgName() + " has shut down server");

                threads[i].getIs().close();
                threads[i].getOs().close();
                threads[i].getClientSocket().close();

            }

            logger.log("SUCCESS", "ChatActions.serverShutdown", "SERVER SHUTDOWN");
            System.exit(0);

        } catch (IOException error) {
            logger.log("ERROR", "ChatActions.serverShutdown", error.toString());
        }
    }

    private void quitChat() {
        logger.log("INFO", "ChatActions.quitChat", "QUIT CHAT --> INITIATED");
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
        logger.log("SUCCESS", "ChatActions.quitChat", "User " + client.getIdNumber() + " removed from user list");

        printToClient("*** Goodbye " + client.getMsgName() + " ***");
        try {
            client.getIs().close();
            client.getOs().close();
            client.getClientSocket().close();
            logger.log("SUCCESS", "ChatActions.quitChat", "QUIT CHAT --> DONE");

        } catch (IOException error) {
            logger.log("ERROR", "ChatActions.quitChat", error.toString());
        }
    }

    private void chatName() {
        logger.log("INFO", "ChatActions.chatName", "ACTION: " + action);
        if (action.equals(commands.get("VIEW_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "VIEW_CHAT_NAME");
            String chatName = chat.getChatName();
            printToClient("CONVERSATION NAME = ");
            printToClient(chatName + "\n");
        } else if (action.equals(commands.get("REMOVE_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "REMOVE_CHAT_NAME");
            chat.resetChatName();
            utils.printToServer(client.getMsgName() + " removed the conversation name.", client.getThreads());
        } else if (action.equals(commands.get("SET_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "SET_CHAT_NAME");
            chat.setChatName(actionMessage);
            utils.printToServer(client.getMsgName() + " changed the conversation name to: " + actionMessage, client.getThreads());
        } else {
            logger.log("INFO", "ChatActions.chatName", "No recognizable action");
            printToClient("ERROR: Please enter a valid action or message");
        }
    }

    private void userManagement() {
        logger.log("INFO", "ChatActions.userManagement", "ACTION: " + action);
        if (action.equals(commands.get("REMOVE_USER"))) {
            logger.log("INFO", "ChatActions.userManagement", "REMOVE_USER");
            removeUser(client.getThreads(), actionMessage);
        } else if (action.equals(commands.get("VIEW_USERS"))) {
            logger.log("INFO", "ChatActions.userManagement", "VIEW_USERS");
            viewUsers();
        }
    }

    private void viewUsers() {
        try {
            logger.log("INFO", "ChatActions.viewUsers", "PRINTING USER LIST");
            printToClient("CURRENT PARTICIPANTS: \n");
            Iterator it = chat.getUsers().entrySet().iterator();
            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry)it.next();
                logger.log("INFO", "ChatActions.viewUsers", "USER --> " + pair.getKey());
                printToClient("| " + pair.getKey().toString() + " - " + chat.getUsers().get(pair.getKey()).getName() + " |\n");
            }
        } catch (Error error) {
            logger.log("ERROR", "ChatActions.viewUsers", error.toString());
        }
    }

    private void removeUser(clientThread[] threads, String user) {
        boolean removed = false;
        logger.log("INFO", "ChatActions.removeUser", "REMOVE USER --> INITIATED");
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null && threads[i].getClientName()!= null
                    && threads[i].getMsgName().equals(user)) {
                threads[i].getOs().println("You have been removed from the chat by " + client.getMsgName());
                threads[i].getOs().close();
                chat.removeUser(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            logger.log("SUCCESS", "ChatActions.removeUser", "REMOVE USER --> DONE");
        } else {
            logger.log("ERROR", "ChatActions.removeUser", "Invalid user provided");
            printToClient("Please provide a valid user\n");
        }

    }


    public void addUser(String name, clientThread client) {
        logger.log("INFO", "ChatActions.addUser", "ADD USER --> INITIATED");
        chat.addUser(name, client);
        if (!chat.isChatNameModified()) {
            chat.resetChatName();
        }
        logger.log("SUCCESS", "ChatActions.addUser", "ADD USER --> DONE");
    }



}
