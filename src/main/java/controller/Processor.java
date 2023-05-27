package controller;

import dataexchange.ProtocolDecoder.MessageDeserialized;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {
    ExecutorService threadPoolExecutor;

    public Processor(int threadPoolSize) {
        this.threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void process(MessageDeserialized messageDeserialized) {
        
    }
}
