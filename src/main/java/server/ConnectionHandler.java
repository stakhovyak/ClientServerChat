package server;

import commons.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TODO: Add logger.
 */
public class ConnectionHandler implements Callable<Void> {

    // TODO: might be not good idea to make it part of connectionHandler itself?
    private static final List<ConnectionHandler> connectionHandlers = new ArrayList<>();

    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    /**
     * Constructs new ConnectionHandler instance.
     * @param socket client's socket
     * @throws RuntimeException IDK, pretty self-explanatory
     */
    public ConnectionHandler(Socket socket) throws RuntimeException {
        this.socket = socket;
        try {
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            // TODO: Getting client's username, may be a bad way to do it but i don't know any better
            Message message = (Message) objectInputStream.readObject();
            this.clientUsername = message.clientsUsername();

            connectionHandlers.add(this);

            // TODO: Log the connection

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat");

        } catch (IOException e) {
            System.out.println("Error occurred during connection establishing");
        } catch (ClassNotFoundException e) {
            System.out.println("Error getting client's information");
        }
    }

    /**
     * Sends Strings to all connections
     * @param message String got from Message object.
     */
    public void broadcastMessage(String message) {
        List<ConnectionHandler> handlersToRemove = new ArrayList<>();
        connectionHandlers.forEach(connection -> {
            if (!connection.clientUsername.equals(clientUsername)) {
                try {
                    connection.bufferedWriter.write(message);
                    connection.bufferedWriter.newLine();
                    connection.bufferedWriter.flush();
                } catch (IOException e) {
                    // Remove the current ConnectionHandler if an IOException occurs
                    handlersToRemove.add(connection);
                }
            }
        });

        // Remove ConnectionHandlers that encountered an IOException during broadcast
        handlersToRemove.forEach(handler -> {
            connectionHandlers.remove(handler);
            broadcastMessage(handler.clientUsername + " has left the chat");
        });
    }

    /**
     * Constantly reads incoming Message objects,
     * broadcasts them as strings.
     * @return null, forces measure of Void object.
     */
    @Override
    public Void call() {
        try {
            while (socket.isConnected()) {
                Message message = (Message) objectInputStream.readObject();
                if (message != null) {
                    broadcastMessage(message.toString());
                }
            }
        } catch (IOException e) {
            // remove the connection handler from the list of handlers in case of an exception
            connectionHandlers.remove(this);
            broadcastMessage(clientUsername + " has left the chat");
        } catch (ClassNotFoundException e) {
            System.out.println("Error occurred during message transportation");
            closeServices();
        } finally {
            closeServices();
        }
        return null;
    }

    /**
     * Closes all vital services.
     */
    public void closeServices() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Error occurred during closing services");
        }
    }

    /**
     * Getter for client's username.
     * @return Client's username.
     */
    public String getClientUsername() {
        return clientUsername;
    }
}
