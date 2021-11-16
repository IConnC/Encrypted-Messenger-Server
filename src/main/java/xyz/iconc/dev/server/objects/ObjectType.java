package xyz.iconc.dev.server.objects;

public enum ObjectType {
    UNDEFINED(0), MESSAGE(1), CHANNEL(2), ACCOUNT(3);
    private final int typeId;

    public int getTypeId() {
        return this.typeId;
    }

    public static ObjectType fromInteger(int val) {
        for (ObjectType type : ObjectType.values()) {
            if (type.typeId == val) return type;
        }
        return ObjectType.UNDEFINED;
    }

    private ObjectType(int i) {
        typeId = i;
    }
}
