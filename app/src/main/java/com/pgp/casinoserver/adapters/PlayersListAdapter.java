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
import com.pgp.casinoserver.utils.event.eventArgs.PlayersListEventArgs;

import java.util.List;

public class PlayersListAdapter extends RecyclerView.Adapter<PlayersListAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<Player> players;
    private Context context;
    private Event onClickEvent = new Event();
    private PlayersListAdapter.ViewHolder clickedHolder = null;

    public PlayersListAdapter(Context context, List<Player> players) {
        this.context = context;
        this.players = players;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PlayersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.players_list_item, parent, false);
        return new PlayersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersListAdapter.ViewHolder holder, int position) {
        Player player = players.get(position);
        if (player.IsCasino){
            holder.nameView.setTextColor(context.getResources().getColor(R.color.gold, null));
        }else{
            holder.nameView.setTextColor(context.getResources().getColor(R.color.white, null));
        }
        holder.nameView.setText(player.Name);
        holder.balanceView.setText(Integer.toString(player.Balance));
        holder.idView.setText("ID: " + Long.toString(player.ID));
        holder.player = player;
        String status = "STATUS";
//        if (player.getStatus() == Status.InGame){
//            status = "Играет в " + player.getCurrentInGame().GetName();
//        }
        holder.statusView.setText(status);


        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedHolder != null){
                    //holder.nameView.setBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_dark_background, null));
                    //holder.gamesCount.setBackgroundColor(context.getResources().getColor(androidx.cardview.R.color.cardview_dark_background, null));
                }
                clickedHolder = holder;
                //holder.nameView.setBackgroundColor(context.getResources().getColor(R.color.dark_grey, null));
                //holder.gamesCount.setBackgroundColor(context.getResources().getColor(R.color.dark_grey, null));
                onClickEvent.Fire(new PlayersListEventArgs(holder));
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView, statusView, balanceView, idView;
        private final LinearLayout itemLayout;
        private Player player;
        public ViewHolder(@NonNull View view){
            super(view);
            nameView = view.findViewById(R.id.name);
            statusView = view.findViewById(R.id.status);
            balanceView = view.findViewById(R.id.balance);
            idView = view.findViewById(R.id.playerId);
            itemLayout = view.findViewById(R.id.item);
        }

        public void setPlayer(Player pl){
            player = pl;
        }

        @Nullable
        public Player getPlayer(){
            return player;
        }
    }

    public Event getOnClickEvent(){
        return onClickEvent;
    }
}
