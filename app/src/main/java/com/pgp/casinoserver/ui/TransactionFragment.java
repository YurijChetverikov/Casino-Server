package com.pgp.casinoserver.ui;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.pgp.casinoserver.ui.animations.framgentchange.TransitionButton;

import java.io.IOException;

public class TransactionFragment extends Fragment {

    private Button button;
    private EditText receiverIdView;
    private TextView receiverNameView;
    private EditText amountView;
    private Spinner typeView;
    private EditText descView;
    private TextView comissionView;
    private TextView totalAmountView;


    private Player receiver;

    private View parentView;
    private ArrayAdapter<String> adapter = null;


    private int totalAmount = 0;
    private int amount = 0;

    private int startReceiverId = -1;


    public TransactionFragment(){

    }

    public TransactionFragment(int receiverID){
        this.startReceiverId = receiverID;
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_transaction, container, false);

        button = parentView.findViewById(R.id.confirmButton);
        receiverIdView = parentView.findViewById(R.id.receiver_id);
        receiverIdView.setTransformationMethod(null);
        receiverNameView = parentView.findViewById(R.id.receiver_name);
        amountView = parentView.findViewById(R.id.amount);
        amountView.setTransformationMethod(null);
        typeView = parentView.findViewById(R.id.transactionType);
        descView = parentView.findViewById(R.id.desc);
        comissionView = parentView.findViewById(R.id.transactionCommission);
        totalAmountView = parentView.findViewById(R.id.totalAmount);



        comissionView.setText("Комиссия " +  Byte.toString(DataLoader.Singleton().TransactionComission) + "%");

        adapter = new ArrayAdapter(parentView.getContext(),
                R.layout.simple_spinner_layout, TransactionType.GetNames());

        adapter.setDropDownViewResource(R.layout.simple_spinner_layout);
        typeView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionType type = TransactionType.Get(typeView.getSelectedItemPosition());
                String desc = descView.getText().toString();

                Transaction t = new Transaction();
                t.Sender = DataLoader.Singleton().GetCasinoPlayer();
                t.Receiver = receiver;
                t.Type = type;
                t.Description = desc;
                t.Amount = totalAmount;
                t.SenderPaid = amount;

                int transId = DataLoader.Singleton().getNextTransactionID();

                DataLoader.Singleton().Transactions.put(transId, t);
                DataLoader.Singleton().GetCasinoPlayer().Transactions.add(DataLoader.Singleton().Transactions.get(transId));
                receiver.Transactions.add(DataLoader.Singleton().Transactions.get(transId));
                receiver.Balance += t.Amount;
                DataLoader.Singleton().GetCasinoPlayer().Balance -= t.SenderPaid;
                try {
                    DataLoader.Singleton().WriteBigDataCache(parentView.getContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(parentView.getContext(), "Транзакция выполнена!", Toast.LENGTH_SHORT).show();
            }
        });

        button.setEnabled(false);




        receiverIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (receiverIdView.getText().toString().length() > 0) {
                    onFieldUpdate();
                }else{
                    receiver = null;
                    receiverNameView.setText("");
                    button.setEnabled(false);
                }
            }
        });

        amountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (amountView.getText().toString().length() > 0){
                    amount = Integer.parseInt(amountView.getText().toString());
                    onFieldUpdate();
                }else{
                    amount = 0;
                    amountView.setError(getString(R.string.invalid_money_amount));
                    button.setEnabled(false);
                }
            }
        });


        if (startReceiverId != -1){
            receiverIdView.setText(Integer.toString(startReceiverId));
            onFieldUpdate();
        }


        return parentView;
    }


    private void onFieldUpdate(){
        int id = Integer.parseInt(receiverIdView.getText().toString());
        Player founded = DataLoader.Singleton().GetPlayerById(id);
        if (founded != null){
            if (!founded.IsCasino){
                receiver = founded;
                receiverIdView.setError(null);
                receiverNameView.setText(receiver.Name);

                if (amount > 0){
                    if (DataLoader.Singleton().GetCasinoPlayer().Balance >= amount){
                        amountView.setError(null);
                        float comissionFloat = (float)((amount * DataLoader.Singleton().TransactionComission) / 100);
                        if (comissionFloat < 1 && DataLoader.Singleton().TransactionComission != 0){
                            comissionFloat = 1;
                        }
                        int comissionAmount = (int)Math.ceil(comissionFloat);

                        totalAmount = amount - comissionAmount;
                        totalAmountView.setText("Итого к переводу: " + Integer.toString(totalAmount));
                        comissionView.setText("Комиссия " +  Byte.toString(DataLoader.Singleton().TransactionComission) + "%: " + Integer.toString(comissionAmount));
                        button.setEnabled(true);
                    }else{
                        amountView.setError(getString(R.string.error_not_enough_money));
                        button.setEnabled(false);
                    }
                }else{
                    amountView.setError(getString(R.string.invalid_money_amount));
                    button.setEnabled(false);
                }
            }else{
                receiver = null;
                receiverNameView.setText("");
                receiverIdView.setError(getString(R.string.missing_player_with_id));
                button.setEnabled(false);
            }

        }else{
            receiver = null;
            receiverNameView.setText("");
            receiverIdView.setError(getString(R.string.missing_player_with_id));
            button.setEnabled(false);
        }
    }

}
