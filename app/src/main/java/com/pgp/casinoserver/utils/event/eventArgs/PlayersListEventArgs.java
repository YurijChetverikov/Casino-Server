package com.pgp.casinoserver.utils.event.eventArgs;

import com.pgp.casinoserver.adapters.PlayersListAdapter;

public class PlayersListEventArgs extends EventArgs{
    private PlayersListAdapter.ViewHolder holder;

    public PlayersListEventArgs(PlayersListAdapter.ViewHolder holder){
        this.holder = holder;
    }

    public PlayersListAdapter.ViewHolder GetHolder(){
        return holder;
    }
}
