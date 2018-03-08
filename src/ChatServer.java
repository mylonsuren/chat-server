

import java.io.*;
import java.net.*;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;


public class ChatServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
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
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
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
    private int numParticipants;

    private HashMap<String, String> commands;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;

        this.numParticipants = 0;
        this.commands = new HashMap<String, String>();
        this.commands.put("SHUTDOWN_SERVER", "/shutdown");
        this.commands.put("LEAVE_CHAT", "/quit");
        this.commands.put("VIEW_MEMBERS", "/members");
    }

    public void shutdownServer(String name) {
        try {
            System.out.println(name + " has shut down server");
            System.out.println("Shutting down the server...");
            for (int i = 0; i < numParticipants; i++) {
                threads[i].os.println("Shutting down server...");
                threads[i].os.println(name + " has shut down server");
            }
            System.exit(0);
        } catch (Error error) {
            System.out.println("There was error shutting down the server.");
            System.out.println(error);
        }
    }

    public void viewMembers() {
        try {

            System.out.println("Current Participants: ");
            for (int i = 0; i < numParticipants; i++) {
                System.out.println(threads[i].clientName);
            }

        } catch (Error error) {

        }
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                numParticipants += 1;
            }
        }

        try {

            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            String name;
            while (true) {
                os.println("Enter your name:");
                name = is.readLine().trim();
                if (name.indexOf('@') == -1) {
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
                        break;
                    }
                } catch(Error error) {}

                //shutdown server
                try {
                    if (line.startsWith(commands.get("SHUTDOWN_SERVER"))) {
                        shutdownServer(name);
                    }
                } catch (Error error) {
                    System.out.println("There was error shutting down the server.");
                    System.out.println(error);
                }

                //view participants
                try {
                    if (line.startsWith(commands.get("VIEW_MEMBERS"))) {
                        System.out.println("HERE");
//                        viewMembers();
                        this.os.println("Current Participants: ");
                        for (int i = 0; i < numParticipants; i++) {
                            this.os.println(threads[i].msgName);
                        }
                        continue;
                    }
                } catch (Error error) {
                    System.out.println(error);
                }


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
                                        threads[i].os.println("<PM> [" + name + "] " + words[1]);

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
                                threads[i].os.println("[" + name + "] " + line);
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
                                + " is leaving the chat room !!! ***");
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