package dataexchange.checksums;

import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class CRC16ChecksumTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    Checksum crc16 = new CRC16Checksum();

    @Test
    public void checksum_IfTheSameInput_ShouldBeTheSame() {

        byte[] bytesToCheck1 = "lorem ipsum dolor sit amen".getBytes(StandardCharsets.UTF_8);
        byte[] bytesToCheck2 = "lorem ipsum dolor sit amen".getBytes(StandardCharsets.UTF_8);

        int sum1 = crc16.computeChecksum(bytesToCheck1);
        int sum2 = crc16.computeChecksum(bytesToCheck2);

        assertEquals(sum1, sum2);
    }

    @Test
    public void checksum_GivenNullInput_WillThrowNullPointerException() {
        exceptionRule.expect(NullPointerException.class);
        int sum = crc16.computeChecksum(null);
    }

    @Test
    public void checksum_InputWithDifferentRegister_GivesDifferentOutput() {
        byte[] bytesToCheck = "somebody once told me the world is gonna rock me".getBytes(StandardCharsets.UTF_8);
        byte[] bytesToCheck2 = "soMEBody once told me the WoRld Is gonna rock me".getBytes(StandardCharsets.UTF_8);

        int sum1 = crc16.computeChecksum(bytesToCheck);
        int sum2 = crc16.computeChecksum(bytesToCheck2);

        assertNotEquals(sum1, sum2);
    }
}
