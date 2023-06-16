package controller;

import dataexchange.ProtocolDecoder;
import dataexchange.ProtocolDecoder.MessageDeserialized;

public class Decryptor {
    Processor processor;
    ProtocolDecoder decoder;
    public byte[] keyBytes = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};

    public Decryptor(ProtocolDecoder decoder, Processor processor) {
        this.decoder = decoder;
        this.processor = processor;
    }

    public void decrypt(byte[] message) {
        ProtocolDecoder.PacketParsed packetParsed = decoder.parsePacket(message);
        boolean isChecksumCorrect = decoder.verifyChecksums(packetParsed);
        if (!isChecksumCorrect) {
            this.processor.process(null);
            return;
        }

        byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, keyBytes);
        MessageDeserialized info = decoder.deserializeMessage(decryptedPayload);
        this.processor.process(info);
    }
}
