package controller;

import dataexchange.BaseProtocolDecoder;
import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DecryptorTests {
    static Checksum CRC16 = new CRC16Checksum();
    static BaseProtocolDecoder decoder = new BaseProtocolDecoder(CRC16);
    static Decryptor decryptor = new Decryptor(decoder, 15);
    static Receiver[] mockReceivers = new MockReceiver[15];


    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 15; i++) {
            mockReceivers[i] = new MockReceiver(decryptor);
            int finalI = i;
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    mockReceivers[finalI].receiveMessage();
                    if (j % 20 == 19) System.out.printf("Messages sent: %d%n", j + 1);
                }
            });
        }
    }
}
