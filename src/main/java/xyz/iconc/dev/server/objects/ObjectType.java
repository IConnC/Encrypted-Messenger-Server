package xyz.iconc.dev.server.objects;

public enum ObjectType {
    UNDEFINED(0), MESSAGE(1), CHANNEL(2), ACCOUNT(3);
    private final int typeId;

    public int getTypeId() {
        return this.typeId;
    }

    private ObjectType(int i) {
        typeId = i;
    }
}
