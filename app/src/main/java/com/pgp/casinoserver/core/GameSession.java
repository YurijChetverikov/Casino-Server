package com.pgp.casinoserver.core;

import java.util.ArrayList;

public class GameSession {
    public ArrayList<GameDataBlock> GameBlocks = new ArrayList<GameDataBlock>(0);
    public long StartDate;
    public long EndDate;
    public int StartBalance;
    public int BalanceChange;
    public int WinsCount;
    public int LoseCount;

    public GameSession(long startDate){
        this.StartDate = startDate;
    }
}
