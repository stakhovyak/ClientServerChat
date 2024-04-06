package client;

import commons.FormInputStream;
import commons.FormPrintStream;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientForm extends JFrame {

    private JTextArea messageArea;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private FormInputStream formInputStream;
    private FormPrintStream formPrintStream;

    public ClientForm() {

        setTitle("Chat Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem connectItem = new JMenuItem("Connect");
        JMenuItem leaveItem = new JMenuItem("Leave");

        menu.add(connectItem);
        menu.add(leaveItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        chatArea = new JTextArea();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(panel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        formInputStream = new FormInputStream(messageField);
        formPrintStream = new FormPrintStream(chatArea);
        System.setOut(formPrintStream);
        System.setIn(formInputStream);

        connectItem.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Select your username:");
            String ipAddress = JOptionPane.showInputDialog("Enter IP Address:");
            String port = JOptionPane.showInputDialog("Enter Port Number:");
            // implement the logic
        });

        leaveItem.addActionListener(e -> {
            // Implementation for leaving server
        });

        var scanner = new Scanner(formInputStream);

        sendButton.addActionListener(e -> {
            String message = messageField.getText();


            chatArea.append("You: " + message + "\n");
            messageField.setText("");
        });
    }

    public static void main(String[] args) {
        var client = new ClientForm();
       /* var printStream = new FormPrintStream(client.chatArea);
        var inputStream = new FormInputStream(client.messageField);
        System.setOut(printStream);
        System.setIn(inputStream);
        System.out.println("Hello world!");
        Scanner scanner = new Scanner(System.in);
        var word = scanner.nextLine();
        System.out.println(word); */
    }
}