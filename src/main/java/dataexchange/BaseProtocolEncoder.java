package dataexchange;

import com.cedarsoftware.util.io.JsonWriter;
import dataexchange.ckecksums.Checksum;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BaseProtocolEncoder implements ProtocolEncoder {
    Checksum checksum;

    public BaseProtocolEncoder(Checksum checksum) {
        this.checksum = checksum;
    }

    @Override
    public byte[] serializeMessage(Object pojo, int cType, int bUserId) {
        String json = JsonWriter.objectToJson(pojo);
        byte[] byteArrayPOJO = json.getBytes(Charset.defaultCharset());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        byte[] cTypeBytes = buffer.putInt(cType).array();
        baos.write(cTypeBytes, 0, cTypeBytes.length);
        buffer.clear();
        byte[] bUserIdBytes = buffer.putInt(bUserId).array();
        baos.write(bUserIdBytes, 0, bUserIdBytes.length);
        baos.write(byteArrayPOJO, 0, byteArrayPOJO.length);

        byte[] message = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public byte[] encryptMessage(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, new byte[16]));
            return cipher.doFinal(data);

        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] createPacket(byte[] data, byte bSrc, long bPktId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0x13);
        baos.write(bSrc);

        byte[] packetIdBytes = ByteBuffer.allocate(Long.BYTES).putLong(bPktId).array();
        baos.write(packetIdBytes, 0, packetIdBytes.length);
        byte[] messageLengthBytes = ByteBuffer.allocate(Integer.BYTES).putInt(data.length).array();
        baos.write(messageLengthBytes, 0, messageLengthBytes.length);
        byte[] headersBytes = baos.toByteArray();
        baos.reset();

        int headersChecksum = this.checksum.computeChecksum(headersBytes);
        byte[] headersChecksumBytes = ByteBuffer.allocate(Integer.BYTES).putInt(headersChecksum).array();
        int payloadChecksum = this.checksum.computeChecksum(data);
        byte[] payloadChecksumBytes = ByteBuffer.allocate(Integer.BYTES).putInt(payloadChecksum).array();
        baos.write(headersBytes, 0, headersBytes.length);
        baos.write(headersChecksumBytes, 0, Integer.BYTES);
        baos.write(data, 0, data.length);
        baos.write(payloadChecksumBytes, 0, Integer.BYTES);

        byte[] packet = baos.toByteArray();

        try {
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return packet;
    }
}
