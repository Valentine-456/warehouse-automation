package controller;

public enum Command {
    GET_INVENTORY_QUANTITY(0),
    DEDUCT_INVENTORY(1),
    ADD_INVENTORY(2),
    ADD_PRODUCT_GROUP(3),
    ADD_PRODUCT_NAME_TO_GROUP(4),
    SET_PRODUCT_PRICE(5);

    private final int commandCode;

    Command(int commandCode) {
        this.commandCode = commandCode;
    }

    public int getCommandCode() {
        return commandCode;
    }
}
