package controller;

import java.net.InetAddress;

public class MockSender implements Sender{
    @Override
    public void send(byte[] message, InetAddress target) {
        System.out.println("Message sent!");
    }
}
