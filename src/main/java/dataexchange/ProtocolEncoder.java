package dataexchange;

public interface ProtocolEncoder {
    public byte[] serializeMessage(Object pojo, int cType, int bUserId);

    public byte[] encryptMessage(byte[] data, byte[] key);

    public byte[] createPacket(byte[] data, byte bSrc, long bPktId);
}
