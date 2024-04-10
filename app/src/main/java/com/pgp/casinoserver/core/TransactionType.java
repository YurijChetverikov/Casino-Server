package com.pgp.casinoserver.core;

import java.util.ArrayList;

public enum TransactionType {
    Transfer("Перевод"),
    Gift("Подарок"),
    Fine("Штраф"),
    Contribution("Взнос"),
    Debit("Списание"),
    Withdrawal("Снятие средств"),
    Replenishment("Пополнение средств");


    private final String name;
    private static String[] names = null;

    TransactionType(String s){
        this.name = s;
    }

    public String GetName(){
        return name;
    }

    public static TransactionType Get(int index) {
        switch(index) {
            case 1:
                return Gift;
            case 2:
                return Fine;
            case 3:
                return Contribution;
            case 4:
                return Debit;
            case 5:
                return Withdrawal;
            case 6:
                return Replenishment;
            default:
                return Transfer;
        }
    }

    public static String[] GetNames(){
        if (names == null){
            names = new String[TransactionType.values().length];
            for (int i = 0; i < names.length; i++){
                names[i] = TransactionType.Get(i).GetName();
            }
        }

        return names;

    }
    public static String[] getNamesForClient(){
        if (names == null){
            names = new String[4];
            for (int i = 0; i < names.length; i++){
                names[i] = TransactionType.Get(i).GetName();
            }
        }

        return names;

    }


}
