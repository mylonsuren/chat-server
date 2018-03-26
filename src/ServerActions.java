
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerActions {


    private HashMap<String, String> serverCommands;
    private String action;
    private ChatLog logger;

    public ServerActions() {
        this.logger = new ChatLog();
        this.serverCommands = new HashMap<>();
        this.serverCommands.put("SHUTDOWN", "/shutdown");
        this.serverCommands.put("CLEAR", "/clear");
        this.serverCommands.put("VIEW_USERS", "/view-users");
        this.serverCommands.put("MESSAGES", "/messages");
        this.action = "";
    }

    public void handleAction(String action, clientThread[] clients, int messages) {
        if (action.startsWith(serverCommands.get("SHUTDOWN"))) {
            shutdown();
        } else if (action.startsWith(serverCommands.get("CLEAR"))) {
            logger.log("INFO", "ServerActions.handleAction", "CLEAR LOG");
            clearLog();
        } else if (action.startsWith(serverCommands.get("VIEW_USERS"))) {
            viewUsers(clients);
        } else if (action.startsWith(serverCommands.get("MESSAGES"))) {
            numberOfMessages(messages);
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

}
