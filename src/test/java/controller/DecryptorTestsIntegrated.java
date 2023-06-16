package controller;

import dataexchange.BaseProtocolDecoder;
import dataexchange.BaseProtocolEncoder;
import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

public class DecryptorTestsIntegrated {
    static ExecutorService serverExecutor = Executors.newFixedThreadPool(30);
    CompletionService completionService = new ExecutorCompletionService(serverExecutor);
    static Checksum CRC16 = new CRC16Checksum();
    static BaseProtocolDecoder decoder = new BaseProtocolDecoder(CRC16);
    static BaseProtocolEncoder encoder = new BaseProtocolEncoder(CRC16);
    static MockSender sender = new MockSender();
    static Encryptor encryptor = new Encryptor(encoder);
    static Processor processor = new Processor(encryptor, sender);
    static Decryptor decryptor = new Decryptor(decoder, processor);
    static Receiver[] mockReceivers = new MockReceiver[15];

//    @Test
//    public void controllerServesAllRequests_ShutsDownGracefully() {
//        ExecutorService clients = Executors.newFixedThreadPool(15);
//        for (int i = 0; i < 15; i++) {
//            mockReceivers[i] = new MockReceiver(decryptor, completionService);
//            int finalI = i;
//            clients.execute(() -> {
//                for (int j = 0; j < 100; j++) {
//                    mockReceivers[finalI].receiveMessage();
//                    if (j % 20 == 19) System.out.printf("Messages sent: %d%n", j + 1);
//                }
//            });
//        }
//        clients.shutdown();
//        try {
//            clients.awaitTermination(1000, TimeUnit.MILLISECONDS);
//            serverExecutor.shutdown();
//            serverExecutor.awaitTermination(1, TimeUnit.DAYS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    public void controllerServesAllRequests_NumberOfIncomingRequestsIsTheSame() {
        ExecutorService clients = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 15; i++) {
            mockReceivers[i] = new MockReceiver(decryptor, completionService);
            int finalI = i;
            clients.execute(() -> {
                for (int j = 0; j < 100; j++) {
                    mockReceivers[finalI].receiveMessage();
                    if (j % 20 == 19) System.out.printf("Messages sent: %d%n", j + 1);
                }
            });
        }
        clients.shutdown();
        try {
            clients.awaitTermination(1000, TimeUnit.MILLISECONDS);
            serverExecutor.shutdown();
            serverExecutor.awaitTermination(1, TimeUnit.DAYS);

            int requestsServed = 0;
            boolean errors = false;
            while (requestsServed < 1500 && !errors) {
                Future resultFuture = null;
                resultFuture = completionService.poll(2000, TimeUnit.MILLISECONDS);
                if (resultFuture != null) {
                    requestsServed++;
                } else {
                    errors = true;
                }
            }
            Assert.assertEquals(requestsServed, 1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
