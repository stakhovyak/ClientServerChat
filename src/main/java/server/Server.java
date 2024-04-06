package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class represents server
 * TODO: Add logger
 * TODO: Create GUI with logger window, "close" and "start" buttons
 */
public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Method represents process,
     * which constantly places new connection
     * in separate thread as soon as it has one.
     */
    public void startServer() {
        while (!serverSocket.isClosed()) {
            try {
                if (!serverSocket.isClosed()) {
                    var socket = serverSocket.accept();
                    var connectionHandler = new ConnectionHandler(socket);
                    System.out.println("Connected client " + connectionHandler.getClientUsername());
                    // adding the connection handler to thread pool to run in separate thread.
                    executorService.submit(connectionHandler);
                }
            } catch (IOException e) {
                System.out.println("The error occurred during server start.\n" + e);
                closeServer();
            }
        }
    }

    /**
     * Closes server.
     */
    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("The error occurred during server closing");
        }
    }

    public static void main(String[] args) {
        try {
            var serverSocket = new ServerSocket(5555);
            var server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            System.out.println("Can't start the server");
        }
    }
}
