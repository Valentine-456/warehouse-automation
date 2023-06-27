package dataexchange;

import java.net.InetAddress;

public class Request {
    public final InetAddress address;
    public final int port;
    public MessageDeserialized messageDeserialized = null;
    public PacketParsed packetParsed;

    public Request(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
}
