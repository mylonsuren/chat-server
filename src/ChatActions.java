
import java.net.ServerSocket;
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
        logger.log("INFO", "ChatActions.handleAction",  "action sent to action handler", new Utils().getLineNumber());
        this.client = client;
        this.action = message;
        if (action.startsWith("/chat-name")) {
            parseActionMessage();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE", new Utils().getLineNumber());
            chatName();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE", new Utils().getLineNumber());
        } else if (action.startsWith("/users")) {
            parseActionMessage();
            logger.log("INFO", "ChatActions.handleAction", "parseActionMessage --> DONE", new Utils().getLineNumber());
            userManagement();
        } else if (action.startsWith("/quit")) {
            quitChat();
            logger.log("SUCCESS", "ChatActions.handleAction", client.getIdNumber() + " : QUIT CHAT", new Utils().getLineNumber());
        } else if (action.startsWith("/modify")) {
            parseActionMessage();
            modify();
        } else if (action.startsWith("/shutdown")) {
            serverShutdown();
            logger.log("SUCCESS", "ChatActions.handleAction", "SERVER SHUTDOWN", new Utils().getLineNumber());
        } else if (action.startsWith("/restart")) {
            restartServer();
            logger.log("SUCCESS", "ChatActions.handleAction", "SERVER RESTART", new Utils().getLineNumber());
        } else {
            printToClient("Invalid action, please enter a valid action or message\n");
            logger.log("ERROR", "ChatActions.handleAction", "INVALID ACTION", new Utils().getLineNumber());
            return;
        }
        logger.log("SUCCESS", "ChatActions.handleAction", "ACTION COMPLETED", new Utils().getLineNumber());
    }

    private void parseActionMessage() {
        actionMessage = null;
        String[] message = action.split("\\s", 2);
        logger.log("INFO", "ChatActions.parseActionMessage", "ACTION = " + message[0], new Utils().getLineNumber());
        if (message.length > 1 && message[1] != null) {
            logger.log("INFO", "ChatActions.parseActionMessage", "ACTION PARAMETER = " + message[1], new Utils().getLineNumber());
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    private void printToClient(String message) {
        logger.log("INFO", "ChatActions.printToClient", "PRINT TO CLIENT --> INITIATED", new Utils().getLineNumber());
        client.getOs().print(message);
        logger.log("SUCCESS", "ChatActions.printToClient", "PRINT TO CLIENT --> DONE", new Utils().getLineNumber());
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
        commands.put("RESTART", "/restart");
    }

    private void modify() {
        if (action.equals(commands.get("MODIFY_NAME"))) {
            logger.log("INFO", "ChatActions.modify", "MODIFY_NAME", new Utils().getLineNumber());
            modifyName();
        }
    }

    private void modifyName() {
        if (actionMessage != null) {
            String currentName = client.getMsgName();
            String newName = actionMessage;
            client.setMsgName(newName);
            chat.getUsers().get(client.getIdNumber()).setName(newName);
            logger.log("INFO", "ChatActions.modifyName", "Current user name: " + currentName + ", new user name: " + newName, new Utils().getLineNumber());
        } else {
            printToClient("Please provide a new name as well. (e.g. /modify-name John)\n");
            logger.log("ERROR", "ChatActions.modifyName","No parameter provided", new Utils().getLineNumber());
        }
    }

    private void restartServer() {
        try {
            clientThread[] threads = client.getThreads();

            logger.log("INFO", "ChatActions.restartServer", client.getMsgName() + " has restart server", new Utils().getLineNumber());
            logger.log("INFO", "ChatActions.restartServer", "Shutting down the server", new Utils().getLineNumber());
            for (int i = 0; i < chat.getNumParticipants(); i++) {
                if (threads[i] != null && threads[i].getClientName() != null) {
                    threads[i].getOs().println("Server is being restarted...");
                }

            }

            logger.log("SUCCESS", "ChatActions.restartServer", "SERVER SHUTDOWN", new Utils().getLineNumber());
            ServerSocket testSocket = ChatServer.getServerSocket();
            testSocket.close();
            testSocket = new ServerSocket(2222);
            ChatServer.setServerSocket(testSocket);
            logger.log("SUCCESS", "ChatActions.restartServer", "Server successfully restarted", new Utils().getLineNumber());
            printToClient("Server successfully restarted\n");
        } catch (IOException error) {
            logger.log("ERROR", "ChatActions.restartServer", error.toString(), new Utils().getLineNumber());
        }
    }

    private void serverShutdown() {

        try {
            clientThread[] threads = client.getThreads();

            logger.log("INFO", "ChatActions.serverShutdown",client.getMsgName() + " has shut down server", new Utils().getLineNumber());
            logger.log("INFO", "ChatActions.serverShutdown","Shutting down the server", new Utils().getLineNumber());
            for (int i = 0; i < chat.getNumParticipants(); i++) {
                threads[i].getOs().println("Shutting down server...");
                threads[i].getOs().println(client.getMsgName() + " has shut down server");

                threads[i].getIs().close();
                threads[i].getOs().close();
                threads[i].getClientSocket().close();

            }

            logger.log("SUCCESS", "ChatActions.serverShutdown", "SERVER SHUTDOWN", new Utils().getLineNumber());
            System.exit(0);

        } catch (IOException error) {
            logger.log("ERROR", "ChatActions.serverShutdown", error.toString(), new Utils().getLineNumber());
        }
    }

    private void quitChat() {
        logger.log("INFO", "ChatActions.quitChat", "QUIT CHAT --> INITIATED", new Utils().getLineNumber());
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
        logger.log("SUCCESS", "ChatActions.quitChat", "User " + client.getIdNumber() + " removed from user list", new Utils().getLineNumber());

        printToClient("*** Goodbye " + client.getMsgName() + " ***");
        try {
            client.getIs().close();
            client.getOs().close();
            client.getClientSocket().close();
            logger.log("SUCCESS", "ChatActions.quitChat", "QUIT CHAT --> DONE", new Utils().getLineNumber());

        } catch (IOException error) {
            logger.log("ERROR", "ChatActions.quitChat", error.toString(), new Utils().getLineNumber());
        }
    }

    private void chatName() {
        logger.log("INFO", "ChatActions.chatName", "ACTION: " + action, new Utils().getLineNumber());
        if (action.equals(commands.get("VIEW_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "VIEW_CHAT_NAME", new Utils().getLineNumber());
            String chatName = chat.getChatName();
            printToClient("CONVERSATION NAME = ");
            printToClient(chatName + "\n");
        } else if (action.equals(commands.get("REMOVE_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "REMOVE_CHAT_NAME", new Utils().getLineNumber());
            chat.resetChatName();
            utils.printToServer(client.getMsgName() + " removed the conversation name.", client.getThreads());
        } else if (action.equals(commands.get("SET_CHAT_NAME"))) {
            logger.log("INFO", "ChatActions.chatName", "SET_CHAT_NAME", new Utils().getLineNumber());
            chat.setChatName(actionMessage);
            utils.printToServer(client.getMsgName() + " changed the conversation name to: " + actionMessage, client.getThreads());
        } else {
            logger.log("INFO", "ChatActions.chatName", "No recognizable action", new Utils().getLineNumber());
            printToClient("ERROR: Please enter a valid action or message");
        }
    }

    private void userManagement() {
        logger.log("INFO", "ChatActions.userManagement", "ACTION: " + action, new Utils().getLineNumber());
        if (action.equals(commands.get("REMOVE_USER"))) {
            logger.log("INFO", "ChatActions.userManagement", "REMOVE_USER", new Utils().getLineNumber());
            removeUser(client.getThreads(), actionMessage);
        } else if (action.equals(commands.get("VIEW_USERS"))) {
            logger.log("INFO", "ChatActions.userManagement", "VIEW_USERS", new Utils().getLineNumber());
            viewUsers();
        }
    }

    private void viewUsers() {
        try {
            logger.log("INFO", "ChatActions.viewUsers", "PRINTING USER LIST", new Utils().getLineNumber());
            printToClient("CURRENT PARTICIPANTS: \n");
            Iterator it = chat.getUsers().entrySet().iterator();
            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry)it.next();
                logger.log("INFO", "ChatActions.viewUsers", "USER --> " + pair.getKey(), new Utils().getLineNumber());
                printToClient("| " + pair.getKey().toString() + " - " + chat.getUsers().get(pair.getKey()).getName() + " |\n");
            }
        } catch (Error error) {
            logger.log("ERROR", "ChatActions.viewUsers", error.toString(), new Utils().getLineNumber());
        }
    }

    private void removeUser(clientThread[] threads, String user) {
        boolean removed = false;
        logger.log("INFO", "ChatActions.removeUser", "REMOVE USER --> INITIATED", new Utils().getLineNumber());
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
            logger.log("SUCCESS", "ChatActions.removeUser", "REMOVE USER --> DONE", new Utils().getLineNumber());
        } else {
            logger.log("ERROR", "ChatActions.removeUser", "Invalid user provided", new Utils().getLineNumber());
            printToClient("Please provide a valid user\n");
        }

    }


    public void addUser(String name, clientThread client) {
        logger.log("INFO", "ChatActions.addUser", "ADD USER --> INITIATED", new Utils().getLineNumber());
        chat.addUser(name, client);
        if (!chat.isChatNameModified()) {
            chat.resetChatName();
        }
        logger.log("SUCCESS", "ChatActions.addUser", "ADD USER --> DONE", new Utils().getLineNumber());
    }



}
