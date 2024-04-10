package com.pgp.casinoserver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;

import java.io.IOException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity{


    private Button regiserButton;
    private EditText nameView;
    private EditText balanceView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        nameView = findViewById(R.id.casinoName_field);
        balanceView = findViewById(R.id.casinoBalance_field);
        passwordView = findViewById(R.id.casinoPassword_field);
        regiserButton = findViewById(R.id.reg_button);

        regiserButton.setOnClickListener(new View.OnClickListener() {
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

                                Player player = new Player(0, password, name, balance);
                                player.IsCasino = true;

                                DataLoader.Singleton().Players.add(player);
                                DataLoader.Singleton().CasinoPlayerID = 0;

                                try {
                                    DataLoader.Singleton().WriteAllCahce(getApplicationContext());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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





}
