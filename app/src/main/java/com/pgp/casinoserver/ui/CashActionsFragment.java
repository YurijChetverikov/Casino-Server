package com.pgp.casinoserver.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.pgp.casinoserver.R;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.core.Transaction;
import com.pgp.casinoserver.core.TransactionType;
import com.pgp.casinoserver.loaders.DataLoader;

import java.io.IOException;

public class CashActionsFragment extends Fragment {


    private View parentView;
    private boolean isDeposit;

    private TextView titleView;
    private EditText amountView;
    private TextView comissionView;
    private TextView totalView;
    private Button confirmButton;


    private int amount;
    private int totalAmount;
    private Player player;

    public CashActionsFragment(boolean isDeposit, Player player) {
        this.isDeposit = isDeposit;
        this.player = player;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.withdrawal_layout, container, false);

        amountView = parentView.findViewById(R.id.amount);
        amountView.setTransformationMethod(null);
        comissionView = parentView.findViewById(R.id.comission);
        totalView = parentView.findViewById(R.id.totalAmount);
        confirmButton = parentView.findViewById(R.id.confirmButton);



        Update();


        amountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (amountView.getText().toString().length() > 0) {
                    Update();
                }else{
                    amount = 0;
                    amountView.setError(getString(R.string.invalid_money_amount));
                    confirmButton.setEnabled(false);
                }
            }
        });


      confirmButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              Transaction t = new Transaction();
              t.Amount = totalAmount;
              t.SenderPaid = amount;
              if (!isDeposit){
                  t.Sender = player;
                  t.Sender.Transactions.add(t);
                  t.Receiver = null;
                  t.Sender.Balance -= totalAmount;
                  t.Type = TransactionType.Withdrawal;
              }else{
                  t.Sender = null;
                  t.Receiver = player;
                  t.Receiver.Transactions.add(t);
                  t.Receiver.Balance += totalAmount;
                  t.Type = TransactionType.Replenishment;
              }

              int transId = DataLoader.Singleton().getNextTransactionID();

              DataLoader.Singleton().Transactions.put(transId, t);
              player.Transactions.add(t);

              try {
                  DataLoader.Singleton().WriteBigDataCache(parentView.getContext());
                  DataLoader.Singleton().WriteTableCache(parentView.getContext());
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
              Toast.makeText(parentView.getContext(), "Транзакция выполнена!", Toast.LENGTH_SHORT).show();
          }
      });




        return parentView;
    }

    private void Update(){
        amount = Integer.parseInt(amountView.getText().toString());
        if (amount <= 0) {
            amount = 0;
            amountView.setError(getString(R.string.invalid_money_amount));
            confirmButton.setEnabled(false);
            return;
        }

        confirmButton.setEnabled(true);
        amountView.setError(null);

        if (!isDeposit){
            float comissionFloat = (float)((amount * DataLoader.Singleton().WithdrawalComission) / 100);
            if (comissionFloat < 1 && DataLoader.Singleton().WithdrawalComission != 0){
                comissionFloat = 1;
            }
            int comissionAmount = (int)Math.ceil(comissionFloat);

            totalAmount = amount - comissionAmount;
            totalView.setText("Итого к выводу: " + Integer.toString(totalAmount));
            comissionView.setText("Комиссия " +  Byte.toString(DataLoader.Singleton().WithdrawalComission) + "%: " + Integer.toString(comissionAmount));
        }else{
            float comissionFloat = (float)((amount * DataLoader.Singleton().DepositComission) / 100);
            if (comissionFloat < 1 && DataLoader.Singleton().DepositComission != 0){
                comissionFloat = 1;
            }
            int comissionAmount = (int)Math.ceil(comissionFloat);

            totalAmount = amount - comissionAmount;
            totalView.setText("Итого к пополнению: " + Integer.toString(totalAmount));
            comissionView.setText("Комиссия " +  Byte.toString(DataLoader.Singleton().DepositComission) + "%: " + Integer.toString(comissionAmount));
        }
    }
}
