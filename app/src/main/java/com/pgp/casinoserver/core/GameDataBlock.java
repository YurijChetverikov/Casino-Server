package com.pgp.casinoserver.core;

public class GameDataBlock {
    public Game Game; // Game ID
    public long StartDate;
    public long EndDate;
    public int WinsCount;
    public int LoseCount;

    public GameDataBlock(Game game, long startDate){
        this.Game = game;
        this.StartDate = startDate;
    }
}
