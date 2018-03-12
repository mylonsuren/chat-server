

import java.io.*;
import java.net.*;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    public static int numParticipants = 0;

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
            System.out.println("Server is now running on port 2222...");
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }



        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        numParticipants++;
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    System.out.println("Server has reached capacity...");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
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

    private HashMap<String, String> commands;
    private HashMap<Integer, String> specialCharacters;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;


        this.commands = new HashMap<String, String>();
        this.commands.put("SHUTDOWN_SERVER", "/shutdown");
        this.commands.put("LEAVE_CHAT", "/quit");
        this.commands.put("VIEW_MEMBERS", "/members");
        this.commands.put("REMOVE_USER", "/remove");


        this.specialCharacters = new HashMap<Integer, String>();
        this.specialCharacters.put(0, "@");
        this.specialCharacters.put(1, "/");

    }

    public void shutdownServer(String name) {
        try {
            System.out.println(name + " has shut down server");
            System.out.println("Shutting down the server...");
            for (int i = 0; i < ChatServer.numParticipants ; i++) {
                threads[i].os.println("Shutting down server...");
                threads[i].os.println(name + " has shut down server");

                System.out.println("Server shutdown initiated...");
                System.out.println("Server shutting down...");
                System.out.println("Server has shut down...");

            }
            System.exit(0);
        } catch (Error error) {
            System.out.println("There was error shutting down the server.");
            System.out.println(error);
        }
    }

    public void viewMembers() {

        try {
            os.println(("Current Participants: "));
            for (int i = 0; i < ChatServer.numParticipants; i++) {
                os.println(i+1 + " - " + threads[i].msgName);
            }
        } catch (Error error) {
            os.println((error));
        }
    }

    public void removeUser(String user) {
        try {
//            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println(msgName + " removed " + user + " from the chat");
                    }

                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null
                            && threads[i].msgName.equals(user)) {

                        threads[i].os.println("You have been removed from the chat by " + msgName);
                        threads[i].os.close();
                        ChatServer.numParticipants--;
                        break;
                    }
                }
//            }
        } catch (Error error) {

        }
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;



        try {


            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            String name;
            while (true) {
                os.println("Enter your name:");
                name = is.readLine().trim();
                if (name.indexOf(specialCharacters.get(0)) == -1) {
                    break;
                }
                else if (name.indexOf(specialCharacters.get(1)) == -1) {
                    break;
                } else {
                    os.println("The name should not contain '@' character.");
                }

            }


            os.println("Welcome " + name
                    + " to the conversation.\nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        msgName = name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("*** " +  name + " has joined the conversation. ***");
                    }
                }
            }


            while (true) {
                String line = is.readLine();

                // Chat commands

                //quit chat
                try {
                    if (line.startsWith(commands.get("LEAVE_CHAT"))) {
                        ChatServer.numParticipants--;
                        break;
                    }
                } catch(Error error) {}

                //shutdown server
                try {
                    if (line.startsWith(commands.get("SHUTDOWN_SERVER"))) {
                        os.println("You have shutdown the server");
                        shutdownServer(name);
                    }
                } catch (Error error) {
                    System.out.println("There was error shutting down the server.");
                    System.out.println(error);
                }

                //view participants
                try {
                    if (line.startsWith(commands.get("VIEW_MEMBERS"))) {
                        viewMembers();
                        continue;
                    }
                } catch (Error error) {
                    System.out.println(error);
                }

                //remove user
                try {
                    if (line.startsWith(commands.get("REMOVE_USER"))) {
                        String[] message = line.split("\\s", 2);
                        if (message.length > 1 && message[1] != null) {
                            message[1] = message[1].trim();
                            if (!message[1].isEmpty()) {
                                if (message[1].equals(msgName)) {
                                    os.println("Please use /quit to leave the chat");
                                } else {
                                    os.println(msgName + " removed " + message[1] + " from the chat");
                                    removeUser(message[1]);
                                }

                            }
                        }
                        continue;
                    }

                } catch (Error error) {
                    System.out.println(error);
                }

                String msgTime = new SimpleDateFormat("HH:mm").format(new java.util.Date());

                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0])) {
                                        threads[i].os.println(msgTime + " <PM> [" + name + "] " + words[1]);

                                        this.os.println("[" + name + "] (to:" + words[0] + ") " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {

                    synchronized (this) {

                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println(msgTime +  " [" + name + "] " + line);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].os.println("*** The user " + name
                                + " is leaving the chat room ***");
                    }
                }
            }
            os.println("*** Goodbye " + name + " ***");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}