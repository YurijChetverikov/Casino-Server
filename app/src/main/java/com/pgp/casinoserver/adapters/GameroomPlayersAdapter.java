package com.pgp.casinoserver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.utils.event.Event;
import com.pgp.casinoserver.utils.event.eventArgs.GameroomEventArgs;

import java.util.ArrayList;
import java.util.List;

public class GameroomPlayersAdapter extends RecyclerView.Adapter<GameroomPlayersAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private Context context;
    private Event onClickEvent = new Event();

    private ArrayList<Player> players = new ArrayList<Player>(0);
    private GameroomPlayersAdapter.ViewHolder clickedHolder = null;

    public GameroomPlayersAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players = players;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GameroomPlayersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.gameroom_table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.nameView.setText(player.Name);
        holder.player = player;
        holder.idView.setText(Long.toString(player.ID));


        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (clickedHolder != null){
                //    holder.nameView.setBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_dark_background, null));
                //    holder.gamesCount.setBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_dark_background, null));
                //}
                //clickedHolder = holder;
                //holder.nameView.setBackgroundColor(context.getResources().getColor(R.color.dark_grey, null));
                //holder.gamesCount.setBackgroundColor(context.getResources().getColor(R.color.dark_grey, null));
                onClickEvent.Fire(new GameroomEventArgs(holder));
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, idView;
        private Player player = null;

        private boolean isWinning = false;

        final LinearLayout itemLayout;
        public ViewHolder(@NonNull View view){
            super(view);
            nameView = view.findViewById(R.id.name);
            idView = view.findViewById(R.id.id);
            itemLayout = view.findViewById(R.id.item);
        }

        public Player getPlayer(){
            return player;
        }

        public LinearLayout getItemLayout(){
            return itemLayout;
        }

        public boolean isWinning(){
            return isWinning;
        }

        public void setWinning(boolean b){
            isWinning = b;
        }
    }

    public Event getOnClickEvent(){
        return onClickEvent;
    }
}
