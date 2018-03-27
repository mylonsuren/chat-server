
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerActions {


    private HashMap<String, String> serverCommands;
    private String action;
    private ChatLog logger;
    private String actionMessage;
    private clientThread[] clients;

    public ServerActions() {
        this.logger = new ChatLog();
        this.serverCommands = new HashMap<>();
        this.serverCommands.put("SHUTDOWN", "/shutdown");
        this.serverCommands.put("CLEAR", "/clear");
        this.serverCommands.put("VIEW_USERS", "/view-users");
        this.serverCommands.put("MESSAGES", "/messages");
        this.serverCommands.put("CONVERSATION", "/view-conversation");
        this.serverCommands.put("REMOVE_USER", "/remove");

        this.action = "";
    }

    public void handleAction(String action, clientThread[] clients, int messages) {
        this.action = action;
        this.clients = clients;
        if (action.startsWith(serverCommands.get("SHUTDOWN"))) {
            shutdown();
        } else if (action.startsWith(serverCommands.get("CLEAR"))) {
            logger.log("INFO", "ServerActions.handleAction", "CLEAR LOG");
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
                logger.log("ERROR", "ServerActions.handleAction", error.toString());
                System.out.println("\nInvalid parameter given, please leave blank or input integer for '/view-conversation'\n");
            }
        } else if (action.startsWith(serverCommands.get("REMOVE_USER"))) {
            parseActionMessage();
            try {
                if (actionMessage == null) {

                } else {
                    int userID = Integer.parseInt(actionMessage);
                    removeUser(userID);
                }
            } catch (NumberFormatException error) {
                logger.log("ERROR", "ServerActions.handleAction", error.toString());
                System.out.println("\nInvalid parameter given, please input integer for '/remove'\n");
            }
        } else {
            if (action.startsWith("/")) {
                System.out.println("Invalid action, please enter a valid action");
            }
        }
    }

    private void parseActionMessage() {
        actionMessage = null;
        String[] message = action.split("\\s", 2);
        logger.log("INFO", "ServerActions.parseActionMessage", "ACTION = " + message[0]);
        if (message.length > 1 && message[1] != null) {
            logger.log("INFO", "ServerActions.parseActionMessage", "ACTION PARAMETER = " + message[1]);
            message[1] = message[1].trim();
            if (!message[1].isEmpty()) {
                actionMessage = message[1];
                action = message[0];
            }
        }
    }

    public void removeUser(int id) {
        ChatServer.chat.getChat().removeUser(id);
        for (int i = 0; i < clients.length; i++)  {
            if (clients[i].getIdNumber() == id) {
                clients[i].getOs().close();
                break;
            }
        }
        logger.log("SUCCESS", "ServerActions.removeUser", "USER id=" + id + " removed");
    }

    public void shutdown() {
        logger.log("SUCCESS", "ServerActions.shutdown", "Server successfully shutdown");
        System.exit(0);
    }

    public void clearLog() {

        try {
            logger.log("INFO", "ServerActions.clearLog", "Attempting to clear log");
            for (int i = 0; i < 50; i++) {
                System.out.print("\n");
            }
            logger.log("SUCCESS", "ServerActions.clearLog", "Log successfully cleared");
            System.out.println("Please wait 3 seconds before resuming application");
            TimeUnit.SECONDS.sleep(2);
            for (int i = 0; i < 50; i++) {
                System.out.print("\n");
            }
        } catch (InterruptedException error) {
            logger.log("ERROR", "ServerActions.clearLog", error.toString());
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
                System.out.print("| " + pair.getKey().toString() + " - " + ChatServer.chat.getChat().getUsers().get(pair.getKey()).getName().toString() + " |\n");
            }
        }
    }

    public void numberOfMessages (int messages) {
        System.out.println("NUMBER OF MESSAGES = " + messages);
    }

    public void viewConversation(int size) {
        int diff = 0;
        if (size >= ChatServer.conversation.size()) {
            diff = 0;
        } else {
            diff = ChatServer.conversation.size() - size;
        }

        System.out.println("\nCONVERSATION");
        for (int i = 0; i < ChatServer.conversation.size(); i++) {
            if (i >= diff) {
                System.out.println("[" + ChatServer.conversation.get(i).getTime() + "] "
                        + ChatServer.conversation.get(i).getUser().getName() +  " : "
                        + ChatServer.conversation.get(i).getMessage());
            }

        }
        System.out.println();
    }

}


class Message {

    private Utils utils;

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
