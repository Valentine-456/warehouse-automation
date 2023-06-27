package network.UDP;

import controller.Decryptor;
import controller.Receiver;
import controller.Sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreUDPServer implements Receiver, Sender {
    private Decryptor decryptor;
    ExecutorService threadPoolExecutor;
    DatagramSocket socket;

    public StoreUDPServer(int nThreads) {
        this.threadPoolExecutor = Executors.newFixedThreadPool(nThreads);
    }

    public void setDecryptor(Decryptor decryptor) {
        this.decryptor = decryptor;
    }

    public void listen(int PORT) {
        try {
            System.out.println("UDP server started on port " + PORT + "...");
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
        try {
            InetAddress address = packet.getAddress();
            byte[] addressBytes = address.getAddress();
            int port = packet.getPort();
            byte[] portBytes = ByteBuffer.allocate(Integer.BYTES).putInt(port).array();
            byte[] packetContent = packet.getData();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(addressBytes, 0, addressBytes.length);
            baos.write(portBytes, 0, portBytes.length);
            baos.write(packetContent, 0, packetContent.length);
            byte[] requestBytes = baos.toByteArray();
            baos.reset();
            baos.close();

            this.decryptor.decrypt(requestBytes);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(byte[] message, InetSocketAddress target) {
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, target);
            this.socket.send(packet);
        } catch (IOException e) {
            System.err.println("The error happened while sending response");
            throw new RuntimeException(e);
        }
    }
}
