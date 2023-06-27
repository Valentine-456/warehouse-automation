package controller;

import java.net.InetSocketAddress;

public interface Sender {
    // TODO: change parameter to InetSocketAddress
    void send(byte[] message, InetSocketAddress target);
}
