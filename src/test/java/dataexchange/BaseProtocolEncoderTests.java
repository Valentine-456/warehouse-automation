package dataexchange;

import com.cedarsoftware.util.io.JsonWriter;
import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BaseProtocolEncoderTests {
    Checksum checksumCalculator = new CRC16Checksum();
    ProtocolEncoder encoder = new BaseProtocolEncoder(checksumCalculator);
    ProductPOJO apples = new ProductPOJO("Apples Golden", "Fruits&Vegetables", 1.99);
    int cType = 66;
    int bUserId = 10;
    byte bSrc = 100;
    long bPktId = 287643;

    @Test
    public void messageSerialization_HasCorrectParameter_CType() {
        byte[] payload = this.encoder.serializeMessage(apples, cType, bUserId);

        int cTypeSerialized = ByteBuffer.wrap(payload, 0, 4).getInt();

        Assert.assertEquals(cType, cTypeSerialized);
    }

    @Test
    public void messageSerialization_HasCorrectParameter_BUserId() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);

        int bUserIdSerialized = ByteBuffer.wrap(payload, 4, 8).getInt();

        Assert.assertEquals(bUserId, bUserIdSerialized);
    }

    @Test
    public void messageSerialization_HasCorrect_Payload() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);

        byte[] stringBytes = new byte[payload.length - 8];
        System.arraycopy(payload, 8, stringBytes, 0, stringBytes.length);
        String applesSerialized = new String(stringBytes);

        Assert.assertEquals(applesSerialized, JsonWriter.objectToJson(apples));
    }

    @Test
    public void packetCreation_HasCorrect_MagicByte() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        byte magicByte = 0x13;
        byte receivedMagicByte = packet[0];

        Assert.assertEquals(magicByte, receivedMagicByte);
    }

    @Test
    public void packetCreation_HasCorrect_bSrc() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        byte received_bSRC = packet[1];

        Assert.assertEquals(bSrc, received_bSRC);
    }

    @Test
    public void packetCreation_HasCorrect_bPktId() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        long received_bPktId = ByteBuffer.wrap(packet, 2, Long.BYTES).getLong();

        Assert.assertEquals(bPktId, received_bPktId);
    }

    @Test
    public void packetCreation_HasCorrect_payloadLength() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        int payloadLength = ByteBuffer.wrap(packet, 10, Integer.BYTES).getInt();

        Assert.assertEquals(payloadLength, payload.length);
    }

    @Test
    public void packetCreation_HasCorrect_headersChecksum() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        int headersBytesLength = 14;
        int received_headersChecksum = ByteBuffer.wrap(packet, headersBytesLength, 4).getInt();
        byte[] received_headers = Arrays.copyOfRange(packet, 0, headersBytesLength);
        ;
        int checksum1 = checksumCalculator.computeChecksum(received_headers);

        Assert.assertEquals(checksum1, received_headersChecksum);
    }

    @Test
    public void packetCreation_HasCorrect_messageChecksum() {
        byte[] payload = encoder.serializeMessage(apples, cType, bUserId);
        byte[] encryptedPayload = encoder.encryptMessage(payload, new byte[]{});
        byte[] packet = encoder.createPacket(encryptedPayload, bSrc, bPktId);

        int receivedMessageChecksum = ByteBuffer.wrap(packet, packet.length - 4, 4).getInt();
        int checksum2 = checksumCalculator.computeChecksum(encryptedPayload);

        Assert.assertEquals(checksum2, receivedMessageChecksum);
    }
}
