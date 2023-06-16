package controller;

import dataexchange.BaseProtocolEncoder;
import dataexchange.ProductPOJO;
import dataexchange.ProtocolEncoder;
import dataexchange.ckecksums.CRC16Checksum;

import java.util.Random;
import java.util.concurrent.CompletionService;

public class MockReceiver implements Receiver {
    private final Decryptor decryptor;
    ProtocolEncoder encoder = new BaseProtocolEncoder(new CRC16Checksum());
    ProductPOJO pojo = new ProductPOJO("Apples Golden", "Fruits&Vegetables", 1.99);
    Random random = new Random();
    CompletionService threadPoolExecutor;

    public MockReceiver(Decryptor decryptor, CompletionService executor) {
        this.decryptor = decryptor;
        this.threadPoolExecutor = executor;
    }

    @Override
    public void receiveMessage() {
        int cType = random.nextInt(1, 101);
        int bUserId = random.nextInt(1, 500);
        byte[] payload = encoder.serializeMessage(pojo, cType, bUserId);

        byte[] encryptedPayload = encoder.encryptMessage(payload, this.decryptor.keyBytes);

        byte bSrc = (byte) random.nextInt(0, 99);
        long bPktId = random.nextLong(0, 1000000);
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        this.threadPoolExecutor.submit(() -> {
            decryptor.decrypt(packet);
            return 0;
        });
    }
}
