package network.UDP;

import controller.Decryptor;
import controller.Receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreUDPServer implements Receiver {
    private final Decryptor decryptor = null;
    ExecutorService threadPoolExecutor;
    DatagramSocket socket;

    public StoreUDPServer(int nThreads) {
//        this.decryptor = decryptor;
        this.threadPoolExecutor = Executors.newFixedThreadPool(nThreads);
    }

    public void listen(int PORT) {
        try {
            this.socket = new DatagramSocket(PORT);
            receiveMessage();
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Socket exception");
        } finally {
            if (socket != null) socket.close();
        }
    }

    @Override
    public void receiveMessage() {
        while (true) {
            byte[] message = new byte[512];
            DatagramPacket p = new DatagramPacket(message, message.length);
            try {
                this.socket.receive(p);
                this.threadPoolExecutor.execute(() -> {
                    handleRequest(p);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleRequest(DatagramPacket packet) {
        String dString = new Date().toString();
        byte[] buf = dString.getBytes();
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
