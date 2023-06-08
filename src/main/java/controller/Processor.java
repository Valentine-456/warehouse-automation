package controller;

import dataexchange.ProtocolDecoder.MessageDeserialized;

public class Processor {
    Encryptor encryptor;
    Sender sender;

    public Processor(Encryptor encryptor, Sender sender) {
        this.encryptor = encryptor;
        this.sender = sender;
    }

    public void process(MessageDeserialized messageDeserialized) {
        if (messageDeserialized == null) try {
            throw new Exception("Damaged or tampered packet received");
        } catch (Exception e) {
            sender.send(new byte[]{}, null);
            return;
        }
        String response = "Ok! Message Received";
        MessageDeserialized responseMessage =
                new MessageDeserialized(response, messageDeserialized.cType, messageDeserialized.bUserId);
        byte[] responsePayload = this.encryptor.encrypt(responseMessage);
        this.sender.send(responsePayload, null);
    }
}
