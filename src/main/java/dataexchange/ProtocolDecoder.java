package dataexchange;

public interface ProtocolDecoder {
    public PacketParsed parsePacket(byte[] packetData);

    public boolean verifyChecksums(PacketParsed packetParsed);

    public byte[] decryptMessage(byte[] data, byte[] key);

    public MessageDeserialized deserializeMessage(byte[] data);

    class PacketParsed {
        byte[] rawHeaders;
        byte bSrc;
        long bPktId;

        byte[] messageEncrypted;
        int headersChecksum;
        int payloadChecksum;

        public PacketParsed(
                byte[] rawHeaders,
                byte bSrc,
                long bPktId,
                byte[] messageEncrypted,
                int headersChecksum,
                int payloadChecksum
        ) {
            this.rawHeaders = rawHeaders;
            this.bPktId = bPktId;
            this.bSrc = bSrc;
            this.messageEncrypted = messageEncrypted;
            this.headersChecksum = headersChecksum;
            this.payloadChecksum = payloadChecksum;
        }
    }

    class MessageDeserialized {
        Object pojo;
        int cType;
        int bUserId;

        public MessageDeserialized(Object pojo, int cType, int bUserId) {
            this.bUserId = bUserId;
            this.pojo = pojo;
            this.cType = cType;
        }
    }
}
