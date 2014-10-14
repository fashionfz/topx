package models;

public enum Gender {
    MAN, WOMAN;

    public static Gender getByOrdinal(int ordinal) {
        Gender[] values = Gender.values();
        if (ordinal < 0 || ordinal > (values.length - 1)) {
            return null;
        } else {
            return values[ordinal];
        }
    }
}