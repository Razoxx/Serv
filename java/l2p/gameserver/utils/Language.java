package l2p.gameserver.utils;

public enum Language {

    ENGLISH(1, "en"), CHINESE(4, "zh"), RUSSIAN(8, "ru");

    public static final Language[] VALUES = values();
    private int _clientIndex;
    private String _shortName;

    private Language(int clientIndex, String shortName) {
        _clientIndex = clientIndex;
        _shortName = shortName;
    }

    public String getShortName() {
        return _shortName;
    }

    public static Language valueOf(int index) {
        for (Language l : VALUES) {
            if (l._clientIndex == index) {
                return l;
            }
        }
        return null;
    }

    public static Language findByShortName(String shortName) {
        for (Language l : VALUES) {
            if (l.getShortName().equals(shortName)) {
                return l;
            }
        }
        return null;
    }
}
