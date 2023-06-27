package network.UDP;

import com.cedarsoftware.util.io.JsonWriter;
import controller.Command;
import dataexchange.*;
import dataexchange.ckecksums.CRC16Checksum;
import dbService.StorePOJO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StoreUDPClient {
    byte[] encryptionKey = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};
    private final ProtocolEncoder encoder = new BaseProtocolEncoder(new CRC16Checksum());
    private final ProtocolDecoder decoder = new BaseProtocolDecoder(new CRC16Checksum());
    int bPktId = 1;
    int bUserId;
    byte bSrc;

    public StoreUDPClient(byte bSrc, int bUserId) {
        this.bUserId = bUserId;
        this.bSrc = bSrc;
    }

    public void sendMessage(StorePOJO storePOJO, Command command) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            // send request
            byte[] buf = this.createMessage(storePOJO, command);

            InetAddress address = InetAddress.getByName(null);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            // get response
            byte[] responseBuf = new byte[512];
            packet = new DatagramPacket(responseBuf, responseBuf.length);
            socket.receive(packet);

            // display response
            byte[] response = packet.getData();
            this.receiveMessage(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) socket.close();
        }
    }

    private void receiveMessage(byte[] response) {
        PacketParsed packetParsed = decoder.parsePacket(response);
        boolean isChecksumCorrect = decoder.verifyChecksums(packetParsed);
        if (!isChecksumCorrect) {
            System.err.println("Checksum is not correct");
        }
        byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, this.encryptionKey);
        MessageDeserialized message = decoder.deserializeMessage(decryptedPayload);
        if (message.bUserId == this.bUserId && this.bSrc == packetParsed.bSrc) {
            System.out.println(JsonWriter.objectToJson(message.pojo));
        } else System.out.println("Wrong packet received!");

    }


    public byte[] createMessage(StorePOJO storePOJO, Command command) {
        byte[] serializedMessage = encoder.serializeMessage(
                storePOJO,
                command.getCommandCode(),
                this.bUserId
        );
        byte[] messageEncrypted = encoder.encryptMessage(serializedMessage, encryptionKey);
        byte[] packet = encoder.createPacket(messageEncrypted, this.bSrc, this.bPktId);
        this.bPktId++;

        return packet;
    }
}
