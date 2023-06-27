package controller;

import dataexchange.PacketParsed;
import dataexchange.ProtocolDecoder;
import dataexchange.Request;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Decryptor {
    Processor processor;
    ProtocolDecoder decoder;
    public byte[] keyBytes = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};

    public Decryptor(ProtocolDecoder decoder, Processor processor) {
        this.decoder = decoder;
        this.processor = processor;
    }

    public void decrypt(byte[] message) {
        try {
            byte[] inetAddressBytes = Arrays.copyOfRange(message, 0, 4);
            byte[] portBytes = Arrays.copyOfRange(message, 4, 8);
            int port = ByteBuffer.wrap(portBytes).getInt();
            byte[] packetData = Arrays.copyOfRange(message, 8, message.length);

            Request request = new Request(InetAddress.getByAddress(inetAddressBytes), port);
            PacketParsed packetParsed = decoder.parsePacket(packetData);
            request.packetParsed = packetParsed;

            boolean isChecksumCorrect = decoder.verifyChecksums(packetParsed);
            if (!isChecksumCorrect) {
                this.processor.process(request);
                return;
            }

            byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, keyBytes);
            request.messageDeserialized = decoder.deserializeMessage(decryptedPayload);
            this.processor.process(request);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
}
