package dbService;

import controller.Decryptor;
import controller.Encryptor;
import controller.Processor;
import dataexchange.BaseProtocolDecoder;
import dataexchange.BaseProtocolEncoder;
import dataexchange.ckecksums.CRC16Checksum;
import dataexchange.ckecksums.Checksum;
import network.UDP.StoreUDPServer;

public class StartStoreUDPServer {
    public static void main(String[] args) {
        StoreUDPServer server = new StoreUDPServer(60);
        Checksum CRC16 = new CRC16Checksum();
        Encryptor encryptor = new Encryptor(new BaseProtocolEncoder(CRC16));
        Processor processor = new Processor(encryptor, server);
        Decryptor decryptor = new Decryptor(new BaseProtocolDecoder(CRC16), processor);
        server.setDecryptor(decryptor);
        server.listen(4445);
    }
}