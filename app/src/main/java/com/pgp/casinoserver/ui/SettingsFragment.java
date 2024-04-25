package com.pgp.casinoserver.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.pgp.casinoserver.R;
import com.pgp.casinoserver.adapters.GamesListAdapter;
import com.pgp.casinoserver.adapters.PlayersListAdapter;
import com.pgp.casinoserver.core.Game;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.utils.event.eventArgs.GamesListEventArgs;
import com.pgp.casinoserver.utils.event.eventArgs.PlayersListEventArgs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private View parentView;

    private RecyclerView gamesView;
    private Button addGameButton;
    private TextInputEditText comissionView;
    private TextInputEditText withdrawalComissionView;
    private TextInputEditText depositComissionView;
    private ImageView casinoImage;

    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_settings, container, false);

        addGameButton = parentView.findViewById(R.id.add_game_button);
        comissionView = parentView.findViewById(R.id.comission_field);
        comissionView.setTransformationMethod(null);
        withdrawalComissionView = parentView.findViewById(R.id.withdrawal_comission);
        withdrawalComissionView.setTransformationMethod(null);
        depositComissionView = parentView.findViewById(R.id.deposit_comission);
        depositComissionView.setTransformationMethod(null);
        gamesView = parentView.findViewById(R.id.games_list);
        casinoImage = parentView.findViewById(R.id.imageView);


        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.dialog_layout, null);
                TextInputEditText input = dialogView.findViewById(R.id.inputView);
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(parentView.getContext())
                        .setTitle(getString(R.string.title_game_adding))
                        .setView(dialogView)
                        .setPositiveButton(getString(R.string.action_add), null)
                        .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                        .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (input.getText().toString().length() > 0){
                                    if (tryToAddGame(input.getText().toString())){
                                        alertDialog.dismiss();
                                        updateGamesList();
                                    }else{
                                        input.setError("Достигнуто максимальное число игр!");
                                    }
                                }else{
                                    input.setError(getString(R.string.invalid_game_name));
                                }
                            }
                        });
                    }
                });



                alertDialog.show();
            }
        });

        comissionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (comissionView.getText().toString().length() > 0){
                    byte newComission = Byte.parseByte(comissionView.getText().toString());

                    if (newComission != DataLoader.Singleton().TransactionComission){
                        DataLoader.Singleton().TransactionComission = newComission;
                        try {
                            DataLoader.Singleton().WriteTableCache(parentView.getContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        comissionView.setText(Byte.toString(DataLoader.Singleton().TransactionComission));


        withdrawalComissionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (withdrawalComissionView.getText().toString().length() > 0){
                    byte newComission = Byte.parseByte(withdrawalComissionView.getText().toString());

                    if (newComission != DataLoader.Singleton().WithdrawalComission){
                        DataLoader.Singleton().WithdrawalComission = newComission;
                        try {
                            DataLoader.Singleton().WriteTableCache(parentView.getContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        withdrawalComissionView.setText(Byte.toString(DataLoader.Singleton().WithdrawalComission));

        depositComissionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (depositComissionView.getText().toString().length() > 0){
                    byte newComission = Byte.parseByte(depositComissionView.getText().toString());

                    if (newComission != DataLoader.Singleton().DepositComission){
                        DataLoader.Singleton().DepositComission = newComission;
                        try {
                            DataLoader.Singleton().WriteTableCache(parentView.getContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        depositComissionView.setText(Byte.toString(DataLoader.Singleton().DepositComission));

        updateGamesList();

        casinoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        if (DataLoader.Singleton().getCasinoBitmap() != null){
            casinoImage.setImageBitmap(DataLoader.Singleton().getCasinoBitmap());
        }




        return parentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // Когда мы выбрали картинку
            if (data != null){
                try {
                    //Toast.makeText(getContext(), getString(R.string.info_loading), Toast.LENGTH_LONG).show();
                    InputStream inputStream = parentView.getContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    DataLoader.Singleton().setCasinoBitmap(bitmap);
                    DataLoader.Singleton().WriteCasinoImageCache(parentView.getContext());
                    casinoImage.setImageBitmap(DataLoader.Singleton().getCasinoBitmap());
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void onGameClicked(@NonNull GamesListEventArgs x) {
        View dialogView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.dialog_layout, null);
        TextInputEditText input = dialogView.findViewById(R.id.inputView);
        input.setText(x.GetHolder().getGame().Name);
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(parentView.getContext())
                .setTitle(getString(R.string.title_game_renaming))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.action_edit), null)
                .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                .create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String newName = input.getText().toString();
                        if (newName != x.GetHolder().getGame().Name){
                            if (newName.length() > 0){
                                x.GetHolder().getGame().Name = newName;
                                alertDialog.dismiss();
                                try {
                                    DataLoader.Singleton().WriteTableCache(parentView.getContext());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                updateGamesList();
                            }else{
                                input.setError(getString(R.string.invalid_game_name));
                            }
                        }else{
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });


        alertDialog.show();
    }

    private void updateGamesList(){
        GamesListAdapter adapter = new GamesListAdapter(parentView.getContext(), DataLoader.Singleton().Games);
        adapter.getOnClickEvent().AddListener((x) -> onGameClicked((GamesListEventArgs) x));
        gamesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        gamesView.setAdapter(adapter);
    }


    private boolean tryToAddGame(@NonNull String name){
        if (name.length() > 0){
            byte freeId = DataLoader.Singleton().GetNextFreeGameID();
            if (freeId != 255){
                Game g = new Game();
                g.ID = freeId;
                g.Name = name;
                DataLoader.Singleton().Games.add(g);
                try {
                    DataLoader.Singleton().WriteTableCache(parentView.getContext());
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return false;
    }
}