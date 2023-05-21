package dataexchange;

import dataexchange.ProtocolDecoder.MessageDeserialized;
import dataexchange.ProtocolDecoder.PacketParsed;
import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;
import org.junit.Assert;
import org.junit.Test;

public class BaseProtocolDecoderTests {
    Checksum checksumCalculator = new CRC16Checksum();
    ProtocolDecoder decoder = new BaseProtocolDecoder(checksumCalculator);

    ProtocolEncoder encoder = new BaseProtocolEncoder(checksumCalculator);
    ProductPOJO apples = new ProductPOJO("Apples Golden", "Fruits&Vegetables", 1.99);
    int cType = 66;
    int bUserId = 10;
    byte bSrc = 100;
    long bPktId = 287643;

    byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
    byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
    byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

    @Test
    public void packetParsing_UnpacksHeadersCorrectly() {
        PacketParsed packetParsed = decoder.parsePacket(packet);

        boolean correct_bSrc = packetParsed.bSrc == bSrc;
        boolean correct_bPktId = packetParsed.bPktId == bPktId;

        Assert.assertTrue(correct_bPktId && correct_bSrc);
    }

    @Test
    public void verifyChecksums_UncorruptedMessage_ReturnsTrue() {
        PacketParsed packetParsed = decoder.parsePacket(packet);

        Assert.assertTrue(decoder.verifyChecksums(packetParsed));
    }

    @Test
    public void verifyChecksums_CorruptedMessage_ReturnsFalse() {
        PacketParsed packetParsed = decoder.parsePacket(packet);

        packetParsed.messageEncrypted = new byte[]{0x4e, 0x7a, (byte) 0xf7, (byte) 0x81, (byte) 0xfd, 0x72, (byte) 0xc7, 0x15, 0x6b, 0x19};

        Assert.assertFalse(decoder.verifyChecksums(packetParsed));
    }

    @Test
    public void verifyChecksums_CorruptedHeaders_ReturnsFalse() {
        PacketParsed packetParsed = decoder.parsePacket(packet);

        packetParsed.rawHeaders = new byte[]{0x4e, 0x7a, (byte) 0xf7, (byte) 0x81, (byte) 0xfd, 0x72, (byte) 0xc7, 0x15, 0x6b, 0x19};

        Assert.assertFalse(decoder.verifyChecksums(packetParsed));
    }

    @Test
    public void deserializeMessage_UnpacksParametersCorrectly() {
        PacketParsed packetParsed = decoder.parsePacket(packet);
        decoder.verifyChecksums(packetParsed);
        byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, new byte[]{});
        MessageDeserialized message = decoder.deserializeMessage(decryptedPayload);

        Assert.assertEquals(message.bUserId, bUserId);
        Assert.assertEquals(message.cType, cType);
    }

    @Test
    public void deserializeMessage_JsonToPOJO_ObjectsHaveSameValues() {
        PacketParsed packetParsed = decoder.parsePacket(packet);
        decoder.verifyChecksums(packetParsed);
        byte[] decryptedPayload = decoder.decryptMessage(packetParsed.messageEncrypted, new byte[]{});
        MessageDeserialized message = decoder.deserializeMessage(decryptedPayload);

        ProductPOJO applesReceived = (ProductPOJO) message.pojo;

        Assert.assertEquals(apples.getName(), applesReceived.getName());
        Assert.assertEquals(apples.getCategory(), applesReceived.getCategory());
        Assert.assertEquals(apples.getPrice(), applesReceived.getPrice(), 0.0);
    }
}
