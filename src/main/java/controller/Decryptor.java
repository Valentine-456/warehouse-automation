package controller;

import dataexchange.ProtocolDecoder;
import dataexchange.ProtocolDecoder.MessageDeserialized;

import java.util.concurrent.*;

public class Decryptor {
    ExecutorService threadPoolExecutor;
    ProtocolDecoder decoder;
    public byte[] keyBytes = new byte[]{0x63, (byte) 0x9a, (byte) 0xfc, (byte) 0x94, 0x36, (byte) 0xba, 0x33, 0x47, 0x63, 0x21, (byte) 0xd9, (byte) 0xe4, 0x35, (byte) 0x90, (byte) 0x9f, (byte) 0xf4};

    public Decryptor(ProtocolDecoder decoder, int threadPoolSize) {
        this.decoder = decoder;
        this.threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void decrypt(byte[] message) {
        Future<MessageDeserialized> future = (Future<MessageDeserialized>) this.threadPoolExecutor.submit(
                () -> {
                    ProtocolDecoder.PacketParsed packetParsed = decoder.parsePacket(message);
                    boolean isChecksumCorrect = decoder.verifyChecksums(packetParsed);
                    if(!isChecksumCorrect) return null;

                    byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, keyBytes);
                    TimeUnit.MILLISECONDS.sleep(100);
                    return decoder.deserializeMessage(decryptedPayload);
                });

        try {

            MessageDeserialized command = future.get();

//            System.out.println(command.cType + command.bUserId);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


    }
}
