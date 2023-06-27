package dataexchange;

public interface ProtocolEncoder {
    byte[] serializeMessage(Object pojo, int cType, int bUserId);

    byte[] encryptMessage(byte[] data, byte[] key);

    byte[] createPacket(byte[] data, byte bSrc, long bPktId);
}
