package controller;

import dataexchange.MessageDeserialized;
import dataexchange.Request;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Processor {
    Encryptor encryptor;
    Sender sender;

    public Processor(Encryptor encryptor, Sender sender) {
        this.encryptor = encryptor;
        this.sender = sender;
    }

    public void process(Request request) {
        int port = request.port;
        InetAddress address = request.address;
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        MessageDeserialized messageDeserialized = request.messageDeserialized;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (messageDeserialized == null) {
            MessageDeserialized responseMessage = new MessageDeserialized(
                    "Damaged or tampered packet received",
                    messageDeserialized.cType,
                    messageDeserialized.bUserId
            );
            byte[] responsePayload = this.encryptor.encrypt(responseMessage, request.packetParsed);
            sender.send(responsePayload, socketAddress);
            return;
        }
        String response = "Ok! Message Received " + messageDeserialized.bUserId;
        MessageDeserialized responseMessage =
                new MessageDeserialized(response, messageDeserialized.cType, messageDeserialized.bUserId);
        byte[] responsePayload = this.encryptor.encrypt(responseMessage, request.packetParsed);
        this.sender.send(responsePayload, socketAddress);
    }

//    public void process(MessageDeserialized messageDeserialized) {
//        if (messageDeserialized == null) try {
//            throw new Exception("Damaged or tampered packet received");
//        } catch (Exception e) {
//            sender.send(new byte[]{}, null);
//            return;
//        }
//        String response = "Ok! Message Received";
//        MessageDeserialized responseMessage =
//                new MessageDeserialized(response, messageDeserialized.cType, messageDeserialized.bUserId);
//        byte[] responsePayload = this.encryptor.encrypt(responseMessage);
//        this.sender.send(responsePayload, null);
//    }
}
