package dataexchange;

public class MessageDeserialized {
    public Object pojo;
    public int cType;
    public int bUserId;

    public MessageDeserialized(Object pojo, int cType, int bUserId) {
        this.bUserId = bUserId;
        this.pojo = pojo;
        this.cType = cType;
    }
}