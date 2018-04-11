


import jdk.jshell.execution.Util;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.ArrayList;
import static org.junit.Assert.*;


public class ChatServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    public static int numParticipants = 0;

    public static ChatActions chat = new ChatActions();
    public static AutoModerator mod = new AutoModerator(chat);
    public static ChatLog logger = new ChatLog();
    public static ServerActions server = new ServerActions(chat);
    public static Message message;


    public static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static int messages;
    public static ArrayList<Message> conversation = new ArrayList<>();

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            String portNumberString = Integer.toString(portNumber);
            logger.log("INFO","ChatServer.main", "java MultiThreadChatServerSync <" + portNumberString + ">", new Utils().getLineNumber());
            logger.log("INFO","ChatServer.main", "Now using port number=" + portNumberString, new Utils().getLineNumber());
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            logger.log("ERROR", "ChatServer.main", e.toString(), new Utils().getLineNumber());
        }

        Thread thread1 = new Thread (() -> {
            while (true) {
                try {
                    String text = input.readLine();
                    logger.log("INFO", "ChatServer.main | Thread 1", "INPUT = " + text, new Utils().getLineNumber());
                    server.handleAction(text, threads, messages);
                } catch (IOException error) {
                    logger.log("ERROR", "ChatServer.main | Thread 1", error.toString(), new Utils().getLineNumber());
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    int i = 0;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new clientThread(clientSocket, threads)).start();
                            logger.log("INFO", "ChatServer.main | Thread 2", "New client created", new Utils().getLineNumber());
                            numParticipants++;
                            break;
                        }
                    }
                    if (i == maxClientsCount) {
                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
                        os.println("Server too busy. Try later.");
                        logger.log("INFO", "ChatServer.main | Thread 2", "Server has reached capacity", new Utils().getLineNumber());
                        os.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (!(e.toString().contains("SocketException"))) {
                        logger.log("ERROR", "ChatServer.main | Thread 2", e.toString(), new Utils().getLineNumber());
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void setServerSocket(ServerSocket serverSocket) {
        ChatServer.serverSocket = serverSocket;
        try {
            serverSocket.close();
        } catch (IOException error) {
            logger.log("ERROR", "ChatServer.setServerSocket", error.toString(), new Utils().getLineNumber());
        }
        ChatServer.main(new String[0]);
    }

    public static void serServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException error) {
            logger.log("ERROR", "ChatServer.setServerSocket", error.toString(), new Utils().getLineNumber());
        }
        ChatServer.main(new String[0]);
    }

    public static clientThread[] getThreads() {
        return threads;
    }
}


class clientThread extends Thread {

    private String clientName = null;
    private String msgName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;
    private int idNumber;

    private String input;

    private HashMap<String, String> commands;
    private HashMap<Integer, String> specialCharacters;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;

        this.commands = new HashMap<>();
        this.commands.put("SHUTDOWN_SERVER", "/shutdown");
        this.commands.put("LEAVE_CHAT", "/quit");
        this.commands.put("VIEW_MEMBERS", "/members");
        this.commands.put("REMOVE_USER", "/remove");
        this.commands.put("CHANGE_CHAT_NAME", "/change-chat-name");
        this.commands.put("VIEW_CHAT_NAME", "/view-chat-name");
        this.commands.put("RESET_CHAT_NAME", "/remove-chat-name");

        this.specialCharacters = new HashMap<>();
        this.specialCharacters.put(0, "@");
        this.specialCharacters.put(1, "/");

    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DataInputStream getIs() {
        return is;
    }

    public PrintStream getOs() {
        return os;
    }

    public clientThread[] getThreads() {
        return threads;
    }

    public String getClientName() {
        return clientName;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }


    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {


            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            ArrayList<String> inputs = new ArrayList<>();
            inputs.add("message");
            inputs.add("/users-view");
            inputs.add("/chat-name-set testing123");
            inputs.add("/chat-name-view");
            inputs.add("/restart");
            inputs.add("/quit");
            int inputIndex = 0;

            while (true) {
                ChatServer.logger.log("INFO", "clientThread.run", "NAME PROMPT", new Utils().getLineNumber());
                os.println("Enter your name:");
                msgName = is.readLine().trim();
                if (!msgName.contains(specialCharacters.get(0)) && !msgName.contains(specialCharacters.get(1))) {
                    break;
                } else {
                    os.println("The name should not contain '@' or '/' characters.");
                }

            }


            os.println("Welcome " + msgName
                    + " to the conversation.\nTo leave enter /quit in a new line.");

            ChatServer.chat.addUser(msgName, this);
            String listOfUsers = ChatServer.chat.getChat().getUsers().toString();
            ChatServer.logger.log("INFO", "clientThread.run", "LIST OF USERS: " + listOfUsers, new Utils().getLineNumber());

            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + msgName;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("*** " +  msgName + " has joined the conversation. ***");
                    }
                }
            }


            while (true) {

                String line = is.readLine();
                this.input = line;

                // Chat commands
                if (this.input.startsWith("/")) {
                    ChatServer.logger.log("INFO", "clientThread.run", "ACTION ITEM", new Utils().getLineNumber());
                    ChatServer.chat.handleAction(this.input, this);
                    continue;
                }

                String msgTime = new Utils().getTime("SHORT_DATE");

                ChatServer.messages += 1;
                // private message
                if (this.input.startsWith("@")) {
                    ChatServer.logger.log("INFO", "clientThread.run", "PRIVATE MESSAGE ENACTED", new Utils().getLineNumber());
                    String[] words = this.input.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0])) {
                                        threads[i].os.println("----------------------------------------");
                                        this.os.println("----------------------------------------");
                                        threads[i].os.println(msgTime + " <PM> [" + msgName + "] " + words[1]);
                                        this.os.println("[" + msgName + "] (to:" + words[0] + ") " + words[1]);
                                        threads[i].os.println("----------------------------------------");
                                        this.os.println("----------------------------------------");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {

                    synchronized (this) {
                        ChatServer.mod.checkMessage(this.input, this);
                        this.input = ChatServer.mod.censor(this.input);
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println(msgTime +  " [" + msgName + "] " + this.input);
                            }
                        }

                        ChatServer.conversation.add(new Message(this.input, ChatServer.chat.getChat().getUser(this.idNumber)));
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            if (!e.toString().contains("SocketException")) {
                ChatServer.logger.log("ERROR", "clientThread.run", e.toString(), new Utils().getLineNumber());
            }
        }
    }
}

