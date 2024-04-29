package com.pgp.casinoserver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.core.Transaction;
import com.pgp.casinoserver.utils.event.Event;
import com.pgp.casinoserver.utils.event.eventArgs.TransactionsTableEventArgs;

import java.util.ArrayList;

public class TransactionsTableAdapter extends RecyclerView.Adapter<TransactionsTableAdapter.ViewHolder>{


    private final LayoutInflater inflater;
    private Player player;
    private Context context;

    private Event onClickEvent = new Event();;

    public TransactionsTableAdapter(Context context, Player player) {
        this.player = player;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public TransactionsTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.trans_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(TransactionsTableAdapter.ViewHolder holder, int position) {
        Transaction t = player.Transactions.get(position);
        //holder.descView.setText(t.getDesc());
        //holder.nameView.setText(t.buildString(DataLoader.Singleton().getPlayer()));
       // holder.typeView.setText(t.getType().GetName());

        String amountString = "";
        String titleString = t.Type.GetName();
        String receiverString = "";
        if (t.Sender != null){
            if(t.Sender.ID == player.ID){
                amountString = "- ";
                amountString+= Integer.toString(t.SenderPaid);
                if (t.Receiver != null){
                    titleString+= " игроку " + t.Receiver.Name;
                    receiverString = Integer.toString( t.Receiver.ID);
                }
            }
        }
        if (t.Receiver != null){
            if (t.Receiver.ID == player.ID){
                amountString = "+ ";
                amountString+= Integer.toString(t.Amount);
                if (t.Sender != null) {
                    titleString += " от " + t.Sender.Name;
                    receiverString = Integer.toString(t.Sender.ID);
                }
                holder.amountView.setTextColor(context.getResources().getColor(R.color.light_green, null));
            }
        }
        amountString+= " ♣";
        holder.setTransaction(t);
        holder.amountView.setText(amountString);
        holder.senderView.setText(receiverString);
        holder.titleView.setText(titleString);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEvent.Fire(new TransactionsTableEventArgs(holder));
            }
        });
    }

    @Override
    public int getItemCount() {
        return player.Transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView, senderView, amountView;
        final LinearLayout itemLayout;
        private Transaction t;
        ViewHolder(View view){
            super(view);
            itemLayout = view.findViewById(R.id.item);
            titleView = view.findViewById(R.id.transTitle);
            senderView = view.findViewById(R.id.transUndertitle);
            amountView = view.findViewById(R.id.transAmount);
        }

        public void setTransaction(Transaction t){
            this.t = t;
        }
        public Transaction getTransaction(){
            return t;
        }
    }

    public Event getOnClickEvent(){
        return onClickEvent;
    }

}
