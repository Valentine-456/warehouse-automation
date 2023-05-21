package dataexchange.checksums;

import dataexchange.BaseProtocolDecoder;
import dataexchange.ProtocolDecoder;
import dataexchange.ckecksums.CRC16Checksum;
import org.junit.Test;

public class BaseProtocolDecoderTests {
    ProtocolDecoder decoder = new BaseProtocolDecoder(new CRC16Checksum());

}
