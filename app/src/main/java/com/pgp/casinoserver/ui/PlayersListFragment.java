package com.pgp.casinoserver.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pgp.casinoserver.R;
import com.pgp.casinoserver.adapters.PlayersListAdapter;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.utils.event.eventArgs.PlayersListEventArgs;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class PlayersListFragment extends Fragment {

    private RecyclerView list;
    private Button createPlayer;

    private View parentView;

    private PlayersListEventArgs event;

    private Context context;

    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        parentView = inflater.inflate(R.layout.fragment_players_list, container, false);
        context = parentView.getContext();

        createPlayer = parentView.findViewById(R.id.addPlayer);
        list = parentView.findViewById(R.id.playersList);

        createPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.dialog3f_layout, null);
                TextInputEditText nameView = dialogView.findViewById(R.id.nameView);
                TextInputEditText balanceView = dialogView.findViewById(R.id.balanceView);
                TextInputEditText passwordView = dialogView.findViewById(R.id.passwordView);
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(parentView.getContext())
                        .setTitle(getString(R.string.create_player_title))
                        .setView(dialogView)
                        .setPositiveButton(getString(R.string.action_create),null)
                        .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                if (Objects.requireNonNull(balanceView.getText()).toString().length() > 0){
                                    balanceView.setError(null);
                                    int balance = Integer.parseInt(balanceView.getText().toString());
                                    if (Objects.requireNonNull(nameView.getText()).toString().length() == 0 || Objects.requireNonNull(nameView.getText()).toString().isEmpty()){
                                        nameView.setError(getString(R.string.invalid_name));
                                    }else{
                                        nameView.setError(null);
                                        if (passwordView.getText().toString().length() == 4){
                                            int password = Integer.parseInt(passwordView.getText().toString());
                                            if (password != 0){
                                                String name = nameView.getText().toString();

                                                Player pl = new Player(DataLoader.Singleton().GetNextFreeID(), password, name, balance);
                                                pl.RegistrationDate = new Date();
                                                DataLoader.Singleton().Players.add(pl);
                                                alertDialog.dismiss();
                                                try {
                                                    DataLoader.Singleton().WriteTableCache(parentView.getContext());
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                updateList();
                                            }else{
                                                passwordView.setError(getString(R.string.invalid_password_content));
                                            }
                                        }else{
                                            passwordView.setError(getString(R.string.invalid_password_length));
                                        }
                                    }
                                }else{
                                    balanceView.setError(getString(R.string.invalid_balance));
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });


        updateList();
        return parentView;
    }


    private void updateList(){
        PlayersListAdapter adapter = new PlayersListAdapter(context, DataLoader.Singleton().Players);
        adapter.getOnClickEvent().AddListener((x) -> onPlayerClicked((PlayersListEventArgs) x));
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
    }

    private void onPlayerClicked(@NonNull PlayersListEventArgs eventArgs){
        //TODO:
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        View view =  inflater.inflate(R.layout.player_bottom_sheet, null);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        dialog.show();

        Player pl = eventArgs.GetHolder().getPlayer();

        TextView nameView = view.findViewById(R.id.name);
        TextView idView = view.findViewById(R.id.id);
        TextView passwordView = view.findViewById(R.id.password);
        TextView balanceView = view.findViewById(R.id.balance);
        TextView joinDateView = view.findViewById(R.id.joinDate);
        //RecyclerView transactionView = view.findViewById(R.id.list);
        Button closeButton = view.findViewById(R.id.close);
        Button editPlayerButton = view.findViewById(R.id.editPlayer);
        Button openTransactionsButton = view.findViewById(R.id.openTransactions);
        Button addToGameroom = view.findViewById(R.id.addToGameroom);
        Button makeTransaction = view.findViewById(R.id.makeTransaction);

        if (DataLoader.Singleton().PlayersInGameRoom.contains(pl)){
            addToGameroom.setEnabled(false);
            addToGameroom.setText("Уже в игре");
        }

        if (pl.IsCasino){
            addToGameroom.setEnabled(false);
            addToGameroom.setVisibility(View.GONE);
            makeTransaction.setEnabled(false);
            makeTransaction.setVisibility(View.GONE);
            nameView.setTextColor(context.getResources().getColor(R.color.gold, null));
        }

        nameView.setText(pl.Name);
        balanceView.setText("Баланс: " + Integer.toString(pl.Balance));
        idView.setText("ID: " + Long.toString(pl.ID));
        passwordView.setText("Пароль: ****"); //

        joinDateView.setText("Дата вступления: " + pl.GetRegistrationDateString());


        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordView.setText("Пароль: " + pl.GetPasswordString());
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        makeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                MainActivity.Singleton().OpenFragment(new TransactionFragment(pl.ID));
            }
        });

        openTransactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                MainActivity.Singleton().OpenFragment(new TransactionHistoryFragment(pl));
            }
        });

        editPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                View dialogView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.dialog3f_layout, null);
                TextInputEditText nameView = dialogView.findViewById(R.id.nameView);
                TextInputEditText balanceView = dialogView.findViewById(R.id.balanceView);
                TextInputEditText passwordView = dialogView.findViewById(R.id.passwordView);
                TextInputLayout balanceLayout = dialogView.findViewById(R.id.balanceViewLayout);

                nameView.setText(pl.Name);
                balanceLayout.setHint(getString(R.string.prompt_player_balance));
                balanceView.setText(Integer.toString(pl.Balance));
                passwordView.setText(pl.GetPasswordString());

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(parentView.getContext())
                        .setTitle(getString(R.string.edit_player_title))
                        .setView(dialogView)
                        .setPositiveButton(getString(R.string.action_edit), null)
                        .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Objects.requireNonNull(balanceView.getText()).toString().length() > 0){
                                    balanceView.setError(null);
                                    if (passwordView.getText().toString().length() == 4){
                                        int password = Integer.parseInt(passwordView.getText().toString());
                                        if (password != 0){
                                            int balance = Integer.parseInt(balanceView.getText().toString());
                                            if (Objects.requireNonNull(nameView.getText()).toString().length() == 0 || Objects.requireNonNull(nameView.getText()).toString().isEmpty()){
                                                nameView.setError(getString(R.string.invalid_name));
                                            }else{
                                                nameView.setError(null);
                                                String name = nameView.getText().toString();

                                                Player player = DataLoader.Singleton().GetPlayerById(pl.ID);

                                                player.Name = name;
                                                player.Balance = balance;
                                                player.Password = password;

                                                dialog.dismiss();
                                                try {
                                                    DataLoader.Singleton().WriteTableCache(parentView.getContext());
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                updateList();
                                            }
                                        }else{
                                            passwordView.setError(getString(R.string.invalid_password_content));
                                        }
                                    }else{
                                        passwordView.setError(getString(R.string.invalid_password_length));
                                    }
                                }else{
                                    balanceView.setError(getString(R.string.invalid_balance));
                                }
                            }
                        });
                    }
                });

                alertDialog.show();

                //MainActivity.Singleton().OpenFragment(new EditPlayerFragment(false, eventArgs.GetHolder().getPlayer().ID));
            }
        });

        addToGameroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoader.Singleton().PlayersInGameRoom.add(pl);
                addToGameroom.setEnabled(false);
                addToGameroom.setText("Уже в игре");
            }
        });
    }
}