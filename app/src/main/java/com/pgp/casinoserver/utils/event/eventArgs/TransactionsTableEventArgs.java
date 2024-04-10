package com.pgp.casinoserver.utils.event.eventArgs;


import com.pgp.casinoserver.adapters.TransactionsTableAdapter;

public class TransactionsTableEventArgs extends EventArgs {

    private TransactionsTableAdapter.ViewHolder holder;

    public TransactionsTableEventArgs(TransactionsTableAdapter.ViewHolder holder){
        this.holder = holder;
    }

    public TransactionsTableAdapter.ViewHolder GetHolder(){
        return holder;
    }


}
