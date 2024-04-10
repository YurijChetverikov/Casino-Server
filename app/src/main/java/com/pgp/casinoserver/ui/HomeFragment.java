package com.pgp.casinoserver.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;

public class HomeFragment extends Fragment {


    private TextView balanceView;
    private ImageView imageView;
    private TextView nameView;



    private Context context;

    private View parentView;
    private LayoutInflater inflater;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        parentView = inflater.inflate(R.layout.fragment_home, container, false);
        context = parentView.getContext();

        balanceView = parentView.findViewById(R.id.balanceView);
        nameView = parentView.findViewById(R.id.casinoName);
        imageView = parentView.findViewById(R.id.imageView);

        balanceView.setText(Integer.toString(DataLoader.Singleton().GetCasinoPlayer().Balance) + " ♣");
        nameView.setText(DataLoader.Singleton().GetCasinoPlayer().Name);
        if (DataLoader.Singleton().getCasinoBitmap() != null){
            imageView.setImageBitmap(DataLoader.Singleton().getCasinoBitmap());
        }

        return parentView;

    }
}
