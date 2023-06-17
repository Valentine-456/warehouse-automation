package network.UDP;

import controller.Command;
import dataexchange.BaseProtocolEncoder;
import dataexchange.ProductPOJO;
import dataexchange.ProtocolEncoder;
import dataexchange.ckecksums.CRC16Checksum;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StoreUDPClient {
    byte[] encryptionKey = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};
    private ProtocolEncoder encoder = new BaseProtocolEncoder(new CRC16Checksum());
    int bPktId = 1;
    int bUserId;
    byte bSrc;

    public StoreUDPClient(byte bSrc, int bUserId) {
        this.bUserId = bUserId;
        this.bSrc = bSrc;
    }

    public void sendMessage() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            // send request
            byte[] buf = this.createMessage();

            InetAddress address = InetAddress.getByName(null);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            // get response
            byte[] responseBuf = new byte[512];
            packet = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(packet);

            // display response
            byte[] response = packet.getData();
            System.out.println(new String(response, 0,30));

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) socket.close();
        }
    }


    public byte[] createMessage() {
        ProductPOJO apples = new ProductPOJO("Apples Golden", "Fruits&Vegetables", 1.99);
        byte[] serializedMessage = encoder.serializeMessage(
                apples,
                Command.GET_INVENTORY_QUANTITY.getCommandCode(),
                this.bUserId
        );
        byte[] messageEncrypted = encoder.encryptMessage(serializedMessage, encryptionKey);
        byte[] packet = encoder.createPacket(messageEncrypted, this.bSrc, this.bPktId);
        this.bPktId++;

        return packet;
    }
}
