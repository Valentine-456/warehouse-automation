package dataexchange.ckecksums;

public interface Checksum {
    int computeChecksum(byte[] data);
}
