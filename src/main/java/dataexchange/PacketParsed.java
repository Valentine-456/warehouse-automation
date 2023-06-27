package dataexchange;

public class PacketParsed {
    public byte[] rawHeaders;
    public byte bSrc;
    public long bPktId;

    public byte[] messageEncrypted;
    public int headersChecksum;
    public int payloadChecksum;

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
