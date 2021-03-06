
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerActions {


    private HashMap<String, String> serverCommands;
    private String action;
    private ChatLog logger;
    private String actionMessage;
    private String userMessage;
    private clientThread[] clients;
    private ChatActions chatActions;

    private String allUsers = "all_users";

    public ServerActions(ChatActions chatActions) {
        this.chatActions = chatActions;
        this.logger = new ChatLog();
        this.serverCommands = new HashMap<>();
        this.serverCommands.put("SHUTDOWN", "/shutdown");
        this.serverCommands.put("CLEAR", "/clear");
        this.serverCommands.put("VIEW_USERS", "/view-users");
        this.serverCommands.put("MESSAGES", "/messages");
        this.serverCommands.put("CONVERSATION", "/view-conversation");
        this.serverCommands.put("REMOVE_USER", "/remove");
        this.serverCommands.put("INFO", "/info");
        this.serverCommands.put("MESSAGE_USER", "/message");
        this.serverCommands.put("WARN_USER", "/warn");
        this.serverCommands.put("TIME_BAN", "/time-ban");
        this.serverCommands.put("RESTART", "/restart");

        this.action = "";
    }

    public void handleAction(String action, clientThread[] clients, int messages) {
        this.action = action;
        this.clients = clients;
        if (action.startsWith(serverCommands.get("SHUTDOWN"))) {
            shutdown();
        } else if (action.startsWith(serverCommands.get("CLEAR"))) {
            logger.log("INFO", "ServerActions.handleAction", "CLEAR LOG", new Utils().getLineNumber());
            clearLog();
        } else if (action.startsWith(serverCommands.get("VIEW_USERS"))) {
            viewUsers();
        } else if (action.startsWith(serverCommands.get("MESSAGES"))) {
            numberOfMessages(messages);
        } else if (action.startsWith(serverCommands.get("CONVERSATION"))) {
            parseActionMessage();
            try {
                if (actionMessage == null) {
                    viewConversation(ChatServer.conversation.size());
                } else {
                    int conversationLength = Integer.parseInt(actionMessage);
                    viewConversation(conversationLength);
                }
            } catch (NumberFormatException error) {
                logger.log("ERROR", "ServerActions.handleAction", error.toString(), new Utils().getLineNumber());
                System.out.println("\nInvalid parameter given, please leave blank or input integer for '/view-conversation'\n");
            }
        } else if (action.startsWith(serverCommands.get("REMOVE_USER"))) {
            parseActionMessage();
            try {
                if (actionMessage == null) {
                    logger.log("ERROR", "ServerActions.handleAction", "No user id provided", new Utils().getLineNumber());
                    System.out.println("\nPlease provide a user id for '/remove'");
                } else {
                    int userID = Integer.parseInt(actionMessage);
                    removeUser(userID);
                }
            } catch (NumberFormatException error) {
                logger.log("ERROR", "ServerActions.handleAction", error.toString(), new Utils().getLineNumber());
                System.out.println("\nInvalid parameter given, please input integer for '/remove'\n");
            }
        } else if (action.startsWith(serverCommands.get("INFO"))) {
            viewInfo();
        } else if (action.startsWith(serverCommands.get("MESSAGE_USER"))) {
            parseUserMessage();
            sendUserMessage();
        } else if (action.startsWith(serverCommands.get("WARN_USER"))) {
            parseActionMessage();
            warnUser();
        } else if (action.startsWith(serverCommands.get("TIME_BAN"))) {
            parseActionMessage();
            timeBanUser();
        } else if (action.startsWith(serverCommands.get("RESTART"))) {
            restartServer();
        } else {
            if (action.startsWith("/")) {
                System.out.println("Invalid action, please enter a valid action");
            }
        }
    }

    private void parseActionMessage() {
        actionMessage = null;
        String[] message = action.split("\\s", 2);
        logger.log("INFO", "ServerActions.parseActionMessage", "ACTION = " + message[0], new Utils().getLineNumber());
        if (message.length > 1 && message[1] != null) {
            logger.log("INFO", "ServerActions.parseActionMessage", "ACTION PARAMETER = " + message[1], new Utils().getLineNumber());
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    private void parseUserMessage() {
        try {
            actionMessage = null;
            String[] message = action.split("\\s", 3);
            logger.log("INFO", "ServerActions.parseUserMessage", "ACTION = " + message[0], new Utils().getLineNumber());
            if (message.length > 1 && message[1] != null && message[2] != null) {
                logger.log("INFO", "ServerActions.parseUserMessage", "USER = " + message[1], new Utils().getLineNumber());
                logger.log("INFO", "ServerActions.parseUserMessage", "MESSAGE = " + message[2], new Utils().getLineNumber());
                message[1] = message[1].trim();
                message[2] = message[2].trim();
                if (!message[1].isEmpty() && !message[2].isEmpty()) {
                    userMessage = message[2];
                    actionMessage = message[1];
                    action = message[0];
                }
            }
        } catch (ArrayIndexOutOfBoundsException error) {
            logger.log("ERROR", "ServerActions.parseUserMessage", error.toString(), new Utils().getLineNumber());
        }
    }

    public void restartServer() {
        logger.log("INFO", "ServerActions.restartServer", "Server is being restarted", new Utils().getLineNumber());
        ChatServer.serServerSocket();
        logger.log("SUCCESS", "ServerActions.restartServer", "Server successfully reset", new Utils().getLineNumber());
    }

    public void warnUser() {
        for (clientThread client : clients) {
            if (client != null) {
                if (client.getMsgName().equals(actionMessage)) {
                    new AutoModerator(chatActions).issueWarning(client);
                }
            }

        }
    }

    public void timeBanUser() {
        for (clientThread client : clients) {
            if (client != null) {
                if (client.getMsgName().equals(actionMessage)) {
                    new AutoModerator(chatActions).timeBan(client);
                }
            }

        }
    }

    public void sendUserMessage() {
        boolean messageSent = false;
        final String user = actionMessage;
        final String message = userMessage;


        for (int i = 0; i < clients.length; i++)  {
            try {
                if (clients[i] != null && (clients[i].getMsgName().equals(user) || user.equals(allUsers))) {
                    clients[i].getOs().println("--------");
                    clients[i].getOs().print("[" + new Utils().getTime("SHORT_DATE") + "] ADMINISTRATOR: ");
                    clients[i].getOs().println(message);
                    clients[i].getOs().println("--------");
                    logger.log("SUCCESS", "ServerActions.sendUserMessage", "MESSAGE SENT TO  " + user + " SUCCESSFULLY", new Utils().getLineNumber());
                    messageSent = true;
                }
            } catch (NullPointerException error) {
                logger.log("ERROR", "ServerActions.sendUserMessage", error.toString(), new Utils().getLineNumber());
            }
        }

        if (!messageSent) {
            logger.log("ERROR", "ServerActions.sendUserMessage", "INVALID USER | USER DOES NOT EXIST", new Utils().getLineNumber());
            viewUsers();
        }

    }

    public void removeUser(int id) {

        for (int i = 0; i < clients.length; i++)  {
            if (clients[i] != null && clients[i].getIdNumber() == id) {
                ChatServer.chat.getChat().removeUser(id);
                clients[i].getOs().close();
                logger.log("SUCCESS", "ServerActions.removeUser", "USER id=" + id + " removed", new Utils().getLineNumber());
                return;
            }
        }

        logger.log("ERROR", "ServerActions.removeUser", "USER id=" + id + " invalid", new Utils().getLineNumber());
    }

    public void shutdown() {
        logger.log("SUCCESS", "ServerActions.shutdown", "Server successfully shutdown", new Utils().getLineNumber());
        System.exit(0);
    }

    public void viewInfo() {
        Information info = new Information();
        System.out.println("\n-------");
        System.out.println("Application Information");
        System.out.println("version: " + info.getVersionNo());
        System.out.println("date modified: " + info.getVersionDate());
        System.out.println("author: " + info.getAuthor());
        System.out.println("-------\n");
    }

    public void clearLog() {

        try {
            logger.log("INFO", "ServerActions.clearLog", "Attempting to clear log", new Utils().getLineNumber());
            for (int i = 0; i < 50; i++) {
                System.out.print("\n");
            }
            logger.log("SUCCESS", "ServerActions.clearLog", "Log successfully cleared", new Utils().getLineNumber());
            System.out.println("Please wait 3 seconds before resuming application");
            TimeUnit.SECONDS.sleep(2);
            for (int i = 0; i < 50; i++) {
                System.out.print("\n");
            }
        } catch (InterruptedException error) {
            logger.log("ERROR", "ServerActions.clearLog", error.toString(), new Utils().getLineNumber());
        }

    }

    public void viewUsers() {
        Iterator it = ChatServer.chat.getChat().getUsers().entrySet().iterator();

        if (ChatServer.chat.getChat().getUsers().size() <= 0) {
            System.out.println("NO CURRENT PARTICIPANTS");
        } else {
            System.out.println("CURRENT PARTICIPANTS:");
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.print("| " + pair.getKey().toString() + " - " + ChatServer.chat.getChat().getUsers().get(pair.getKey()).getName() + " |\n");
            }
        }
    }

    public void numberOfMessages (int messages) {
        System.out.println("NUMBER OF MESSAGES = " + messages);
    }

    public void viewConversation(int size) {
        int diff;
        if (size >= ChatServer.conversation.size()) {
            diff = 0;
        } else {
            diff = ChatServer.conversation.size() - size;
        }

        System.out.println("\nCONVERSATION");
        for (int i = 0; i < ChatServer.conversation.size(); i++) {
            if (i >= diff) {
                String time = ChatServer.conversation.get(i).getTime();
                String user;
                try {
                    user = ChatServer.conversation.get(i).getUser().getName();
                } catch (NullPointerException error) {
                    user = "[deleted]";
                }

                String message = ChatServer.conversation.get(i).getMessage();

                System.out.println("[" + time + "] " + user +  " : " + message);
            }

        }
        System.out.println();
    }

}


class Message {

    private String message;
    private User user;
    private String time;

    public Message(String message, User user) {
        this.message = message;
        this.user = user;
        this.time = new Utils().getTime("FULL_DATE");
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

}

class Information {

    private String versionNo = "0.3.1";
    private String versionDate = "04/012/2018";
    private String author = "Mylon S";

    public Information() {}

    public String getAuthor() {
        return author;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public String getVersionNo() {
        return versionNo;
    }

}
