
import java.util.HashMap;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class ChatActions {

    private String action;
    private Chat chat;
    private clientThread client;
    private String actionMessage;

    private ChatLog logger;

    private HashMap<String,String> commands = new HashMap<>();

    public ChatActions() {
        this.chat = new Chat();
        this.logger = new ChatLog();
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
            //System.out.println("parseActionMessage --> DONE");
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
            chatName();
            //System.out.println("chatName --> DONE");
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
        } else if (action.startsWith("/users")) {
            parseActionMessage();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
            userManagement();
        } else if (action.startsWith("/quit")) {
            //System.out.println("quit --> DONE");
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE");
            quitChat();
            logger.log("SUCCESS", "ChatActions.handleAction", client.getIdNumber() + " : QUIT CHAT");
        } else if (action.startsWith("/shutdown")) {
            serverShutdown();
            //System.out.println("serverSHUTDOWN --> DONE");
            logger.log("SUCCESS", "ChatActions.handleAction", "SERVER SHUTDOWN");
        } else {
            //System.out.println("INVALID ACTION");
            printToClient("Invalid action, please enter a valid action or message\n");
            logger.log("ERROR", "ChatActions.handleAction", "INVALID ACTION");
            return;
        }
        logger.log("SUCCESS", "ChatActions.handleAction", "ACTION COMPLETED");

    }

    private void parseActionMessage() {
        String[] message = action.split("\\s", 2);
        //System.out.println("ChatActions.parseActionMessage");
        logger.log("INFO", "ChatActions.parseActionMessage", "ACTION = " + message[0]);
        //System.out.println("ACTION = " + message[0]);
        if (message.length > 1 && message[1] != null) {
            //System.out.println("ACTION PARAMETER = " + message[1]);
            logger.log("INFO", "ChatActions.parseActionMessage", "ACTION PARAMETER = " + message[1]);
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    private void printToClient(String message) {
        //System.out.println("ChatActions.printToClient");
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

        commands.put("LEAVE_CHAT", "/quit");

        commands.put("SHUT_DOWN", "/shutdown");
    }

    private void serverShutdown() {

        try {
            clientThread[] threads = client.getThreads();

            //System.out.println(client.getMsgName() + " has shut down server");
            logger.log("INFO", "ChatActions.serverShutdown",client.getMsgName() + " has shut down server");
            logger.log("INFO", "ChatActions.serverShutdown","Shutting down the server");
            //System.out.println("Shutting down the server...");
            for (int i = 0; i < chat.getNumParticipants(); i++) {
                threads[i].getOs().println("Shutting down server...");
                threads[i].getOs().println(client.getMsgName() + " has shut down server");

                threads[i].getIs().close();
                threads[i].getOs().close();
                threads[i].getClientSocket().close();

            }

            //System.out.println("Server shutdown initiated...");
            //System.out.println("Server shutting down...");
            logger.log("SUCCESS", "ChatActions.serverShutdown", "SERVER SHUTDOWN");
            System.exit(0);

        } catch (IOException error) {
            //System.out.println("Shutdown server failed.");
            logger.log("ERROR", "ChatActions.serverShutdown", error.toString());
            //System.out.println(error);
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

        printToClient("*** Goodbye " + client.getMsgName() + " ***");
        try {
            client.getIs().close();
            client.getOs().close();
            client.getClientSocket().close();
            logger.log("SUCCESS", "ChatActions.quitChat", "QUIT CHAT --> DONE");

        } catch (IOException error) {
            //System.out.println(error);
            logger.log("ERROR", "ChatActions.quitChat", error.toString());
        }

    }

    private void chatName() {
        //System.out.println("ChatActions.chatName");
        logger.log("INFO", "ChatActions.chatName", "ACTION: " + action);
        //System.out.println("ACTION: " + action);
        if (action.equals(commands.get("VIEW_CHAT_NAME"))) {
            //System.out.println("VIEW_CHAT_NAME");
            logger.log("INFO", "ChatActions.chatName", "VIEW_CHAT_NAME");
            String chatName = chat.getChatName();
            printToClient("CONVERSATION NAME = ");
            printToClient(chatName + "\n");
        } else if (action.equals(commands.get("REMOVE_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "REMOVE_CHAT_NAME");
            //System.out.println("REMOVE_CHAT_NAME");
            chat.resetChatName();
        } else if (action.equals(commands.get("SET_CHAT_NAME"))) {
//            System.out.println("SET_CHAT_NAME: " + actionMessage);
            logger.log("INFO", "ChatActions.chatName", "SET_CHAT_NAME");
            chat.setChatName(actionMessage);
        } else {
            //System.out.println("ChatActions.chatName --> NO RESPONSE");
        }
    }

    private void userManagement() {
        logger.log("INFO", "ChatActions.userManagement", "ACTION: " + action);
        if (action.equals(commands.get("REMOVE_USER"))) {
            //System.out.println("REMOVE_USER");
            logger.log("INFO", "ChatActions.userManagement", "REMOVE_USER");
            removeUser(client.getThreads(), actionMessage);
        } else if (action.equals(commands.get("VIEW_USERS"))) {
            //System.out.println("VIEW_USERS");
            logger.log("INFO", "ChatActions.userManagement", "VIEW_USERS");
            viewUsers();
        }
    }

    private void viewUsers() {
        try {
            //System.out.println("ChatActions.viewUsers");
            logger.log("INFO", "ChatActions.viewUsers", "PRINTING USER LIST");
            printToClient("CURRENT PARTICIPANTS: ");
            Iterator it = chat.getUsers().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                printToClient(pair.getKey() + " - " + chat.getUsers().get(pair.getKey()).getName());
            }
        } catch (Error error) {
            //System.out.println(error);
            logger.log("ERROR", "ChatActions.viewUsers", error.toString());
        }
    }

    private void removeUser(clientThread[] threads, String user) {
        logger.log("INFO", "ChatActions.removeUser", "REMOVE USER --> INITIATED");
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
        logger.log("SUCCESS", "ChatActions.removeUser", "REMOVE USER --> DONE");
    }


    public void addUser(String name) {
        //System.out.println("ChatActions.addUser");
        logger.log("INFO", "ChatActions.addUser", "ADD USER --> INITIATED");
        //System.out.println("USER: " + name);

        //System.out.println("USERS: " + chat.getUsers());

        chat.addUser(name);
        if (!chat.isChatNameModified()) {
            chat.resetChatName();
        }
        //System.out.println("USER ADDED --> DONE");
        logger.log("SUCCESS", "ChatActions.addUser", "ADD USER --> DONE");
    }



}
