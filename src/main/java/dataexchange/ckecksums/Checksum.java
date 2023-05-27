package dataexchange.ckecksums;

public interface Checksum {
    public int computeChecksum(byte[] data);
}
