package com.pgp.casinoserver.utils.event.eventArgs;

import com.pgp.casinoserver.adapters.GameroomPlayersAdapter;

public class GameroomEventArgs extends EventArgs{
    private GameroomPlayersAdapter.ViewHolder holder;

    public GameroomEventArgs(GameroomPlayersAdapter.ViewHolder holder){
        this.holder = holder;
    }

    public GameroomPlayersAdapter.ViewHolder GetHolder(){
        return holder;
    }
}
