package com.pgp.casinoserver.net;

public enum PackageType {
    INVALID,
    PLAYER_FULL,
    CASINO_LOGO,
    REMAINING_TRANSACTIONS_COUNT,
    TRANSACTION_COMISSION,
    TRANSACTION_REQUEST,
    PASSWORD,
    GAMES,
    PLAYER,
    TRANSACTIONS_HISTORY,
    CASINO_NAME;




    public static PackageType get(byte index) {
        switch(index) {
            case 1:
                return PLAYER_FULL;
            case 2:
                return CASINO_LOGO;
            case 3:
                return REMAINING_TRANSACTIONS_COUNT;
            case 4:
                return TRANSACTION_COMISSION;
            case 5:
                return TRANSACTION_REQUEST;
            case 6:
                return PASSWORD;
            case 7:
                return GAMES;
            case 8:
                return PLAYER;
            case 9:
                return TRANSACTIONS_HISTORY;
            case 10:
                return CASINO_NAME;
            default:
                return INVALID;
        }
    }


}
