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
import com.pgp.casinoserver.core.Game;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.utils.event.Event;
import com.pgp.casinoserver.utils.event.eventArgs.GamesListEventArgs;
import com.pgp.casinoserver.utils.event.eventArgs.PlayersListEventArgs;

import java.util.ArrayList;
import java.util.List;

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final ArrayList<Game> games;
    private Context context;
    private Event onClickEvent = new Event();
    private GamesListAdapter.ViewHolder clickedHolder = null;

    public GamesListAdapter(Context context, ArrayList<Game> games) {
        this.context = context;
        this.games = games;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GamesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.simple_recycle_layout, parent, false);
        return new GamesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesListAdapter.ViewHolder holder, int position) {
        Game g = games.get(position);
        holder.nameView.setText(g.Name);
        holder.game = g;

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
                onClickEvent.Fire(new GamesListEventArgs(holder));
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final LinearLayout itemLayout;
        private Game game;
        public ViewHolder(@NonNull View view){
            super(view);
            nameView = view.findViewById(R.id.nameView);
            itemLayout = view.findViewById(R.id.item);
        }

        public void setGame(Game g){
            game = g;
        }

        @Nullable
        public Game getGame(){
            return game;
        }
    }

    public Event getOnClickEvent(){
        return onClickEvent;
    }
}
