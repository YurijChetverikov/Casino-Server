package com.pgp.casinoserver.utils.event.eventArgs;

import com.pgp.casinoserver.adapters.GamesListAdapter;

public class GamesListEventArgs extends EventArgs{
    private GamesListAdapter.ViewHolder holder;

    public GamesListEventArgs(GamesListAdapter.ViewHolder holder){
        this.holder = holder;
    }

    public GamesListAdapter.ViewHolder GetHolder(){
        return holder;
    }
}
