package com.pgp.casinoserver.net;

import com.pgp.casinoserver.core.TransactionType;

public enum RequestType {

    INVALID("INV"),
    GET("GET"),
    RESPONSE("RES");

    private final String name;
    private static String[] names = null;

    RequestType(String s){
        this.name = s;
    }

    public String getName(){
        return name;
    }

    public static RequestType get(byte index) {
        switch(index) {
            case 1:
                return GET;
            case 2:
                return RESPONSE;
            default:
                return INVALID;
        }
    }

}
