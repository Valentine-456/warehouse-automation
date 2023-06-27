package controller;

import dataexchange.MessageDeserialized;
import dataexchange.PacketParsed;
import dataexchange.ProtocolEncoder;

public class Encryptor {
    ProtocolEncoder encoder;
    public byte[] keyBytes = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};

    public Encryptor(ProtocolEncoder encoder) {
        this.encoder = encoder;
    }

    public byte[] encrypt(MessageDeserialized message, PacketParsed packetParsed) {
        byte[] payload = encoder.serializeMessage(message.pojo, message.cType, message.bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, this.keyBytes);
        return encoder.createPacket(encryptedPayload, packetParsed.bSrc, packetParsed.bPktId);
    }
}
