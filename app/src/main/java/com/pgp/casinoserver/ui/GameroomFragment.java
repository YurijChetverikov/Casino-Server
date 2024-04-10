package com.pgp.casinoserver.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.adapters.GameroomPlayersAdapter;
import com.pgp.casinoserver.core.Game;
import com.pgp.casinoserver.core.GameDataBlock;
import com.pgp.casinoserver.core.GameSession;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.utils.event.eventArgs.GameroomEventArgs;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class GameroomFragment extends Fragment {

    private RecyclerView list;
    private Button gameButton;
    private Spinner currentGame;


    private Context context;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        View parentView = inflater.inflate(R.layout.fragment_gameroom, container, false);
        context = parentView.getContext();

        list = parentView.findViewById(R.id.list);
        gameButton = parentView.findViewById(R.id.gameButton);
        currentGame = parentView.findViewById(R.id.currentGame);

        updateList();

        ArrayList<String> gamesNames = new ArrayList<>(DataLoader.Singleton().Games.size());

        for(Game g: DataLoader.Singleton().Games){
            gamesNames.add(g.Name);
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter(context,
                R.layout.simple_spinner_layout, gamesNames);

        adapter2.setDropDownViewResource(R.layout.simple_spinner_layout);

        currentGame.setAdapter(adapter2);

        int selected = findCurrentGameIndex();

        if (selected != -1){
            currentGame.setSelection(selected);
        }else{
            Toast.makeText(context, "Нет добавленных игр!", Toast.LENGTH_LONG).show();
        }



        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                GameroomPlayersAdapter.ViewHolder vh = (GameroomPlayersAdapter.ViewHolder)viewHolder;
                if (swipeDir == ItemTouchHelper.RIGHT){
                    // Победа
                    vh.getItemLayout().setBackgroundColor(getResources().getColor(R.color.dark_green));
                    vh.setWinning(true);
                }else if (swipeDir == ItemTouchHelper.LEFT){
                    vh.getItemLayout().setBackgroundColor(getResources().getColor(R.color.dark_blue_4));
                    vh.setWinning(false);
                }
                list.getAdapter().notifyDataSetChanged(); // Нужно чтобы элемент вернулся на своё место после свайпа
                //list.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition()); // Нужно чтобы элемент вернулся на своё место после свайпа
                //Remove swiped item from list and notify the RecyclerView
                //int position = viewHolder.getAdapterPosition();
                //arrayList.remove(position);
                //adapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(list);


        currentGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                // Смена игры => сохраняем сыгранный датаблок у каждого игрока
                for (Player pl: DataLoader.Singleton().PlayersInGameRoom){
                    if (pl.CurrentSession != null){
                        if (pl.CurrentDataBlock != null){
                            pl.CurrentDataBlock.EndDate = new Date().getTime();
                            pl.CurrentSession.GameBlocks.add(pl.CurrentDataBlock);
                            pl.CurrentDataBlock = null;
                        }
                    }
                }
                DataLoader.Singleton().CurrentGame = DataLoader.Singleton().Games.get(selectedItemPosition);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameButton.setEnabled(false);

                // Тут сначала проверяем, есть ли у игрока текущая игровая сессия (то есть играл ли он у нас до этого
                // времени или мы его только что в Gameroom добавили). Далее, если нет сессии - создаём, а потом пишем в текущий
                // датаблок сессии. Если его нет - создаём

                if (DataLoader.Singleton().CurrentGame != null){
                    for (int i = 0; i < list.getAdapter().getItemCount(); i++){
                        GameroomPlayersAdapter.ViewHolder vh = (GameroomPlayersAdapter.ViewHolder)list.findViewHolderForAdapterPosition(i);

                        if (vh.getPlayer().CurrentSession == null){
                            // Создаём сессию, если её нет
                            vh.getPlayer().CurrentSession = new GameSession(new Date().getTime());
                            vh.getPlayer().CurrentSession.StartBalance = vh.getPlayer().Balance;
                        }
                        if (vh.getPlayer().CurrentDataBlock == null){
                            vh.getPlayer().CurrentDataBlock = new GameDataBlock(DataLoader.Singleton().CurrentGame, new Date().getTime());
                        }
                        if (vh.isWinning()){
                            vh.getPlayer().CurrentDataBlock.WinsCount++;
                            vh.getPlayer().CurrentSession.WinsCount++;
                        }else{
                            vh.getPlayer().CurrentDataBlock.LoseCount++;
                            vh.getPlayer().CurrentSession.LoseCount++;
                        }
                        vh.getItemLayout().setBackgroundColor(getResources().getColor(R.color.dark_blue_4));
                        vh.setWinning(false);
                    }
                }

                gameButton.setEnabled(true);
            }
        });


        return parentView;
    }

    private void updateList(){
        GameroomPlayersAdapter adapter = new GameroomPlayersAdapter(context, DataLoader.Singleton().PlayersInGameRoom);
        adapter.getOnClickEvent().AddListener((x) -> onClick((GameroomEventArgs) x));
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
    }

    private void onClick(@NonNull GameroomEventArgs e){
        Player pl = e.GetHolder().getPlayer();
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        View view = inflater.inflate(R.layout.gameroom_bottom_sheet, null);
        dialog.setCancelable(true);
        dialog.setContentView(view);
        dialog.show();

        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.id);
        TextView gamesCount = view.findViewById(R.id.count);
        Button removeButton = view.findViewById(R.id.removeFromButton);

        name.setText(pl.Name);
        id.setText(Integer.toString(pl.ID));
        //String gamesCountStr = "Наиграно в " + DataLoader.Singleton().getCurrentGame().GetName() + " : ";
        //gamesCountStr+= pl.getGamesCount(DataLoader.Singleton().getCurrentGame());
        //gamesCount.setText(gamesCountStr);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Закрываем игровую сессию игрока и удаляем его из комнаты
                if (pl.CurrentSession != null){
                    if (pl.CurrentDataBlock != null){
                        pl.CurrentDataBlock.EndDate = new Date().getTime();
                        pl.CurrentSession.GameBlocks.add(pl.CurrentDataBlock);
                        pl.CurrentDataBlock = null;
                    }
                    pl.CurrentSession.EndDate = new Date().getTime();
                    pl.CurrentSession.BalanceChange = pl.Balance - pl.CurrentSession.StartBalance;
                    pl.GameSessions.add(pl.CurrentSession);
                    pl.CurrentSession = null;
                }
                try {
                    DataLoader.Singleton().WriteDataCahce(context, pl);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                DataLoader.Singleton().PlayersInGameRoom.remove(pl);
                updateList();
                dialog.cancel();
            }
        });

    }

    private int findCurrentGameIndex(){
        if (DataLoader.Singleton().CurrentGame != null){
            for(int i = 0; i < DataLoader.Singleton().Games.size(); i++){
                if (DataLoader.Singleton().Games.get(i).ID == DataLoader.Singleton().CurrentGame.ID){
                    return i;
                }
            }
        }else{
            if (DataLoader.Singleton().Games.size() > 0){
                DataLoader.Singleton().CurrentGame = DataLoader.Singleton().Games.get(0);
                return 0;
            }
        }

        return -1;
    }
}