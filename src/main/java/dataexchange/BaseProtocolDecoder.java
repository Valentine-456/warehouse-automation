package dataexchange;

import com.cedarsoftware.util.io.JsonReader;
import dataexchange.ckecksums.Checksum;

import java.nio.ByteBuffer;

public class BaseProtocolDecoder implements ProtocolDecoder {
    Checksum checksum;

    public BaseProtocolDecoder(Checksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public PacketParsed parsePacket(byte[] packetData) {
        byte[] rawHeaders = new byte[14];
        System.arraycopy(packetData, 0, rawHeaders, 0, rawHeaders.length);
        byte bSrc = packetData[1];
        long bPktId = ByteBuffer.wrap(packetData, 2, 8).getLong();
        int wLen = ByteBuffer.wrap(packetData, 10, 4).getInt();
        int headersChecksum = ByteBuffer.wrap(packetData, 14, 4).getInt();

        byte[] messageEncrypted = new byte[wLen];
        System.arraycopy(packetData, 18, messageEncrypted, 0, messageEncrypted.length);
        int payloadChecksum = ByteBuffer.wrap(packetData, 18 + wLen, 4).getInt();

        return new PacketParsed(rawHeaders, bSrc, bPktId, messageEncrypted, headersChecksum, payloadChecksum);
    }

    @Override
    public boolean verifyChecksums(PacketParsed packetParsed) {
        int headersChecksum = this.checksum.computeChecksum(packetParsed.rawHeaders);
        boolean headersChecksumCorrect = (headersChecksum == packetParsed.headersChecksum);

        int messageChecksum = this.checksum.computeChecksum(packetParsed.messageEncrypted);
        boolean messageChecksumCorrect = (messageChecksum == packetParsed.payloadChecksum);

        return headersChecksumCorrect && messageChecksumCorrect;
    }

    @Override
    public byte[] decryptMessage(byte[] data, byte[] key) {
        return data;
    }

    @Override
    public MessageDeserialized deserializeMessage(byte[] data) {
        int cType = ByteBuffer.wrap(data, 0, 4).getInt();
        int bUserId = ByteBuffer.wrap(data, 4, 4).getInt();

        byte[] jsonMessage = new byte[data.length - 8];
        System.arraycopy(data, 8, jsonMessage, 0, jsonMessage.length);
        String json = new String(jsonMessage);
        Object obj = JsonReader.jsonToJava(json);

        return new MessageDeserialized(obj, cType, bUserId);
    }
}
