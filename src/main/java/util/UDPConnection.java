package util;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDPConnection extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private Consumer<String> messageHandler;

    private static UDPConnection instance;

    private UDPConnection() {
        running = true;
    }

    public static UDPConnection getInstance() {
        if (instance == null) {
            instance = new UDPConnection();
        }
        return instance;
    }

    public void setPort(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                if (messageHandler != null) {
                    messageHandler.accept(message);
                } else {
                    System.out.println("Received: " + message);
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendDatagram(String msg, String ipDest, int portDest) {
        new Thread(() -> {
            try {
                InetAddress ipAddress = InetAddress.getByName(ipDest);
                DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), ipAddress, portDest);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopConnection() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}