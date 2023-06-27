package dataexchange;

public interface ProtocolDecoder {
    PacketParsed parsePacket(byte[] packetData);

    boolean verifyChecksums(PacketParsed packetParsed);

    byte[] decryptMessage(byte[] data, byte[] key);

    MessageDeserialized deserializeMessage(byte[] data);
}
