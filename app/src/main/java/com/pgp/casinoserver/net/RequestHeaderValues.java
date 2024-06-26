package com.pgp.casinoserver.net;

public enum RequestHeaderValues {
    INVALID,
    REQUEST_TYPE,
    PACKAGE_TYPE,
    PLAYER_ID,
    PLAYER_PASSWORD,
    ERROR_CODE,
    LAST_TRANSACTIONS_HISTORY_INDEX;



    public static RequestHeaderValues get(byte index) {
        switch(index) {
            case 1:
                return REQUEST_TYPE;
            case 2:
                return PACKAGE_TYPE;
            case 3:
                return PLAYER_ID;
            case 4:
                return PLAYER_PASSWORD;
            case 5:
                return ERROR_CODE;
            case 6:
                return LAST_TRANSACTIONS_HISTORY_INDEX;
            default:
                return INVALID;
        }
    }
}
