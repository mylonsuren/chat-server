
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerActions {


    private HashMap<String, String> serverCommands;
    private String action;
    private ChatLog logger;
    private String actionMessage;

    public ServerActions() {
        this.logger = new ChatLog();
        this.serverCommands = new HashMap<>();
        this.serverCommands.put("SHUTDOWN", "/shutdown");
        this.serverCommands.put("CLEAR", "/clear");
        this.serverCommands.put("VIEW_USERS", "/view-users");
        this.serverCommands.put("MESSAGES", "/messages");
        this.serverCommands.put("CONVERSATION", "/view-conversation");

        this.action = "";
    }

    public void handleAction(String action, clientThread[] clients, int messages) {
        this.action = action;
        if (action.startsWith(serverCommands.get("SHUTDOWN"))) {
            shutdown();
        } else if (action.startsWith(serverCommands.get("CLEAR"))) {
            logger.log("INFO", "ServerActions.handleAction", "CLEAR LOG");
            clearLog();
        } else if (action.startsWith(serverCommands.get("VIEW_USERS"))) {
            viewUsers(clients);
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

        }
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
            System.out.println("Please wait 5 seconds before resuming application");
            TimeUnit.SECONDS.sleep(5);
            for (int i = 0; i < 50; i++) {
                System.out.print("\n");
            }
        } catch (InterruptedException error) {
            logger.log("ERROR", "ServerActions.clearLog", error.toString());
        }

    }

    public void viewUsers(clientThread[] clients) {
        ArrayList<String> users = new ArrayList<>();
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] != null) {
                users.add(clients[i].getMsgName());
            }
        }

        if (users.size() <= 0) {
            System.out.println("NO USERS");
        } else {
            System.out.println("CONVERSATION PARTICIPANTS:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println(" - " + users.get(i));
            }
        }
    }

    public void numberOfMessages (int messages) {
        System.out.println("NUMBER OF MESSAGES = " + messages);
    }

    public void viewConversation(int size) {
        if (size >= ChatServer.conversation.size()) {
            size = ChatServer.conversation.size();
        }

        System.out.println("\nCONVERSATION");
        for (int i = size-1; i >= 0; i--) {
            System.out.println("[" + ChatServer.conversation.get(i).getTime() + "] "
                    + ChatServer.conversation.get(i).getUser().getName() +  " : "
                    + ChatServer.conversation.get(i).getMessage());
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
