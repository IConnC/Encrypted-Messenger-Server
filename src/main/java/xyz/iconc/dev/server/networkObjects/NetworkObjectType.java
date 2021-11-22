package xyz.iconc.dev.server.networkObjects;

public enum NetworkObjectType {
    UNDEFINED(0), MESSAGE(1), CHANNEL(2), ACCOUNT(3);
    private final int typeId;

    public int getTypeId() {
        return this.typeId;
    }

    public static NetworkObjectType fromInteger(int val) {
        for (NetworkObjectType type : NetworkObjectType.values()) {
            if (type.typeId == val) return type;
        }
        return NetworkObjectType.UNDEFINED;
    }

    private NetworkObjectType(int i) {
        typeId = i;
    }
}
