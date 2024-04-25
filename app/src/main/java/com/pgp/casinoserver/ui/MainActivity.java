package com.pgp.casinoserver.ui;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.databinding.ActivityMainBinding;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.net.PackageType;
import com.pgp.casinoserver.net.Request;
import com.pgp.casinoserver.net.RequestHeader;
import com.pgp.casinoserver.net.Transport;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    private Class<? extends Fragment> currentFragmentId;

    private static MainActivity singleton;


    private Transport transport = null;


    @Nullable
    public static MainActivity Singleton(){
        return singleton;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transport = new Transport(getApplicationContext());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home){
                OpenFragment(new HomeFragment());
            }else if (item.getItemId() == R.id.transactions){
                OpenFragment(new TransactionFragment());
            }else if (item.getItemId() == R.id.transactionsHistory) {
                OpenFragment(new TransactionHistoryFragment(DataLoader.Singleton().GetCasinoPlayer()));
            }else if (item.getItemId() == R.id.settings){
                OpenFragment(new SettingsFragment());
            }else if (item.getItemId() == R.id.playersList){
                OpenFragment(new PlayersListFragment());
            }else if (item.getItemId() == R.id.gameroom){
                OpenFragment(new GameroomFragment());
            }

            return true;
        });

        OpenFragment(new HomeFragment());

        networkInit();

        Player p = DataLoader.Singleton().GetCasinoPlayer();

        Log.e("", "dv");
    }

    @Override
    protected void onDestroy(){
        //transport.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public void OpenFragment(@NonNull Fragment f, boolean changeButtons){
        if (currentFragmentId != f.getClass()){
            currentFragmentId = f.getClass();
            if (changeButtons){
                if (f.getClass().equals(HomeFragment.class)) {
                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                }else if (f.getClass().equals(TransactionFragment.class)){
                    binding.bottomNavigationView.setSelectedItemId(R.id.transactions);
                }else if (f.getClass().equals(TransactionHistoryFragment.class)){
                    binding.bottomNavigationView.setSelectedItemId(R.id.transactionsHistory);
                }else if (f.getClass().equals(SettingsFragment.class)){
                    binding.bottomNavigationView.setSelectedItemId(R.id.settings);
                }else if (f.getClass().equals(PlayersListFragment.class)){
                    binding.bottomNavigationView.setSelectedItemId(R.id.playersList);
                }else if (f.getClass().equals(GameroomFragment.class)){
                    binding.bottomNavigationView.setSelectedItemId(R.id.gameroom);
                }
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.content_frame, f);
            transaction.commit();
        }
    }

    public void OpenFragment(@NonNull Fragment f){
        OpenFragment(f, true);
    }




    private void networkInit(){
        //transport = new Transport();
    }



}