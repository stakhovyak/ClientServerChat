package client;

import commons.FormInputStream;
import commons.Message;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Logger;

/**
 * Represents client
 * TODO: Add logger
 * TODO: figure out how to tie up the GUI with the code.
 */
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private final Socket socket;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;
    private final String username;

    /**
     * Constructs Client instance.
     * @param socket Client's socket.
     * @param username Client's username.
     */
    public Client(Socket socket, String username) {
        logger.info("Constructing new client instance.");
        this.socket = socket;
        this.username = username;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            logger.info("Sending user info to the server.");
            objectOutputStream.writeObject(Message.createMessage(username, ""));

        } catch (IOException e) {
            System.out.println("Error occurred during client initialization.");
            logger.severe("Error occurred during client initialization.");
            closeServices();
        }
    }

    /**
     * The method is some kind of demon
     * which constantly sends the string
     * coming to System.in, if there is one
     * @return null, a forced measure for Void object.
     */
    public Callable<Void> sendMessage() {
        return () -> {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                try {
                    logger.info("Getting message text from System.in");
                    var message = scanner.nextLine();

                    objectOutputStream.writeObject(Message.createMessage(username, message));
                    logger.info("Message sent: " + message);

                } catch (IOException e) {
                    System.out.println("Error occurred during sending the message");
                    logger.severe("Error occurred during sending the message");
                    closeServices();
                    break;
                }
            }
            return null;
        };
    }

    /**
     * The method represents a
     * process, which constantly listens to
     * incoming strings, broadcasted by connectionHandler,
     * and prints it in System.out.
     * @return null, a forced measure for Void object
     */
    public Callable<Void> listenForMessages() {
        return () -> {
            logger.info("Getting the message");
            String message;
            while (socket.isConnected() && (message = bufferedReader.readLine()) != null) {

                logger.info("Received message: " + message);
                System.out.println(message);
            }
            closeServices();
            System.out.println("Session ended.");
            logger.info("Closing listenForMessages().");
            return null;
        };
    }

    /**
     * The method closes all client's services.
     */
    public void closeServices() {
        logger.info("Closing vital services");
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Error occurred during closing services");
            logger.severe("Error occurred during closing services");
        }
    }

    public static void main(String[] args) {
        try {
            var scanner = new Scanner(System.in);
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            // Hardcoded for brevity, will be customizable in GUI version.
            var socket = new Socket("127.0.0.1", 5555);
            var client = new Client(socket, username);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(client.listenForMessages());
            executor.submit(client.sendMessage());

            executor.shutdown();
        } catch (IOException e) {
            System.out.println("Can't connect to the server.");
        }
    }
}
