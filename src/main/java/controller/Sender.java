package controller;

import java.net.InetAddress;

public interface Sender {
    void send(byte[] message, InetAddress target);
}
