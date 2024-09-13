package model;

import util.UDPConnection;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class Peer {
    private String name;
    private UDPConnection connection;
    private String destIP;
    private int destPort;

    public Peer(String name, int listenPort, String destIP, int destPort) throws SocketException {
        this.name = name;
        this.destIP = destIP;
        this.destPort = destPort;

        this.connection = UDPConnection.getInstance();
        this.connection.setPort(listenPort);
        this.connection.setMessageHandler(this::handleReceivedMessage);
    }

    private void handleReceivedMessage(String message) {
        System.out.println(name + " received: " + message);
    }

    public void start() {
        connection.start();
        startSendingMessages();
    }

    private void startSendingMessages() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(name + ", enter message: ");
                String message = scanner.nextLine();
                connection.sendDatagram(message, destIP, destPort);
            }
        }).start();
    }

    public static void main(String[] args) throws SocketException {
        if (args.length != 4) {
            System.out.println("Usage: java Peer <name> <listenPort> <destIP> <destPort>");
            return;
        }

        String name = args[0];
        int listenPort = Integer.parseInt(args[1]);
        String destIP = args[2];
        int destPort = Integer.parseInt(args[3]);

        Peer peer = new Peer(name, listenPort, destIP, destPort);
        peer.start();
    }
}