package com.pgp.casinoserver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.CacheReadingResult;
import com.pgp.casinoserver.loaders.DataLoader;

import java.io.IOException;

public class LoadingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        /*DataLoader.Singleton().Players.add(new Player(0, 1111, "Ваня", 1000));
        DataLoader.Singleton().Players.add(new Player(1, 1111, "Петя", 300));
        DataLoader.Singleton().GetPlayerById(0).IsCasino = true;
        DataLoader.Singleton().CasinoPlayerID = 0;

        try {
            DataLoader.Singleton().WriteAllCahce(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DataLoader.Singleton().Players.clear();*/



    }


    @Override
    protected void onResume(){
        super.onResume();

        // Читаем кэш, если он существует
//        try {
            CacheReadingResult res = DataLoader.Singleton().ReadCache(getApplicationContext());
            if (res == CacheReadingResult.READED_SUCCESSFULLY){
                // Кэш успешно прочитали, можем загружать активити входа
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }else if (res == CacheReadingResult.READED_WITH_ERRORS){
                // Вот такой строчки вообще быть не должно при работе приложения, иначе все данные сотрутся!!!
                Toast.makeText(getApplicationContext(), "Кэш повреждён!", Toast.LENGTH_SHORT).show();
            }else{
                // Кэша нет => создаём новое казино
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
//        } catch (IOException e) {
//            DataLoader.Singleton().Players.clear();
//
//            Log.e("package:mine", "пизда\n" + e.toString());
//        }
    }



}

