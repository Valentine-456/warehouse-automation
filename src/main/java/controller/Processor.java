package controller;

import dataexchange.MessageDeserialized;
import dataexchange.Request;
import dbService.DBSQLServiceFacade;
import dbService.StorePOJO;

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

        int response = this.handleRequest(messageDeserialized);

        MessageDeserialized responseMessage =
                new MessageDeserialized(response, messageDeserialized.cType, messageDeserialized.bUserId);
        byte[] responsePayload = this.encryptor.encrypt(responseMessage, request.packetParsed);
        this.sender.send(responsePayload, socketAddress);
    }

    public int handleRequest(MessageDeserialized messageDeserialized) {
        DBSQLServiceFacade db = new DBSQLServiceFacade("store.db");
        int cType = messageDeserialized.cType;
        StorePOJO storePOJO = (StorePOJO) messageDeserialized.pojo;
        int response = 0;

        try {
            switch (cType) {
                case 0:
                    response = db.GET_INVENTORY_QUANTITY(storePOJO);
                    break;
                case 1:
                    db.DEDUCT_INVENTORY(storePOJO);
                    break;
                case 2:
                    db.ADD_INVENTORY(storePOJO);
                    break;
                case 3:
                    db.ADD_PRODUCT_GROUP(storePOJO);
                    break;
                case 4:
                    db.ADD_PRODUCT_NAME_TO_GROUP(storePOJO);
                    break;
                case 5:
                    db.SET_PRODUCT_PRICE(storePOJO);
                    break;
            }
            return response;
        } catch (Exception e) {
            System.err.println(e);
            return -1;
        }
    }
}
