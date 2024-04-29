package com.pgp.casinoserver.net;

public enum RequestErrorCode {
    GOOD,
    ERROR,
    INVALID_PACKAGE_TYPE,
    DATA_MISSING$PLAYER_ID,
    DATA_MISSING$PLAYER_PASSWORD,
    DATA_MISSING$PACKAGE,
    DATA_MISSING$LAST_TRANSACTIONS_HISTORY_INDEX,
    DATA_MISSING$RESERVED_1,
    DATA_MISSING$RESERVED_4,
    DATA_MISSING$RESERVED_5,
    DATA_MISSING$RESERVED_6,
    DATA_MISSING$RESERVED_7,
    DATA_MISSING$RESERVED_8,
    DATA_NOT_FOUND$PLAYER_WITH_ID,
    DATA_NOT_FOUND$PLAYER_WITH_PASSWORD,
    DATA_NOT_FOUND$RESERVED_1,
    DATA_NOT_FOUND$RESERVED_2,
    DATA_NOT_FOUND$RESERVED_3,
    DATA_NOT_FOUND$RESERVED_4,
    DATA_NOT_FOUND$RESERVED_5,
    DATA_NOT_FOUND$RESERVED_6,
    DATA_NOT_FOUND$RESERVED_7,
    DATA_NOT_FOUND$RESERVED_8,
    REQUEST_DENIED$NOT_ENOUGH_FUNDS;



    public static RequestErrorCode get(byte index) {
        switch(index) {
            case 0:
                return GOOD;
            case 1:
                return ERROR;
            case 2:
                return INVALID_PACKAGE_TYPE;
            case 3:
                return DATA_MISSING$PLAYER_ID;
            case 4:
                return DATA_MISSING$PLAYER_PASSWORD;
            case 5:
                return DATA_MISSING$PACKAGE;
            case 6:
                return DATA_MISSING$RESERVED_1;
            case 7:
                return DATA_MISSING$LAST_TRANSACTIONS_HISTORY_INDEX;
            case 8:
                return DATA_MISSING$RESERVED_1;
            case 9:
                return DATA_MISSING$RESERVED_1;
            case 10:
                return DATA_MISSING$RESERVED_1;
            case 11:
                return DATA_MISSING$RESERVED_1;
            case 12:
                return DATA_MISSING$RESERVED_1;
            case 13:
                return DATA_NOT_FOUND$PLAYER_WITH_ID;
            case 14:
                return DATA_NOT_FOUND$PLAYER_WITH_PASSWORD;
            case 15:
                return DATA_NOT_FOUND$RESERVED_1;
            case 16:
                return DATA_NOT_FOUND$RESERVED_1;
            case 17:
                return DATA_NOT_FOUND$RESERVED_1;
            case 18:
                return DATA_NOT_FOUND$RESERVED_1;
            case 19:
                return DATA_NOT_FOUND$RESERVED_1;
            case 20:
                return DATA_NOT_FOUND$RESERVED_1;
            case 21:
                return DATA_NOT_FOUND$RESERVED_1;
            case 22:
                return DATA_NOT_FOUND$RESERVED_1;
            case 23:
                return REQUEST_DENIED$NOT_ENOUGH_FUNDS;
            default:
                return ERROR;
        }
    }
}
