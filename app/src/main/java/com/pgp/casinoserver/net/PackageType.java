package com.pgp.casinoserver.net;

public enum PackageType {
    INVALID,
    PLAYER_FULL,
    CASINO_LOGO,
    REMAINING_TRANSACTIONS_COUNT,
    COMISSION,
    TRANSACTION_REQUEST,
    PASSWORD;




    public static PackageType get(byte index) {
        switch(index) {
            case 1:
                return PLAYER_FULL;
            case 2:
                return CASINO_LOGO;
            case 3:
                return REMAINING_TRANSACTIONS_COUNT;
            case 4:
                return COMISSION;
            case 5:
                return TRANSACTION_REQUEST;
            case 6:
                return PASSWORD;
            default:
                return INVALID;
        }
    }


}
