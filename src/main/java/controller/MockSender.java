package controller;

import java.net.InetSocketAddress;

public class MockSender implements Sender {
    @Override

    public void send(byte[] message, InetSocketAddress target) {
        System.out.println("Message sent!");
    }
}
