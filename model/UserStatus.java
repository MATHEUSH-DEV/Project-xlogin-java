package model;

public enum UserStatus {
    OFFLINE(0), ONLINE(1), AWAY(2), BANNED(3);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromInt(int i) {
        for (UserStatus s : UserStatus.values()) {
            if (s.getValue() == i) return s;
        }
        return OFFLINE;
    }
}
