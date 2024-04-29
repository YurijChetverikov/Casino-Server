package com.pgp.casinoserver.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.utils.BinaryUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Transaction {
    public Player Sender;
    public Player Receiver;
    public int Amount = 0;
    public int SenderPaid = 0;
    public TransactionType Type;
    public String Description = "";


    public Transaction(){

    }

    // Если true -> все поля заполнены
    public boolean check(){
        if (Sender == null || Receiver == null || SenderPaid == 0) {return false;}

        return true;
    }

    public boolean apply(){
        if (!check()) {return false;}

        // На всякий случай пересчитываем комиссию:

        float comissionFloat = (float)((SenderPaid * DataLoader.Singleton().TransactionComission) / 100);
        if (comissionFloat < 1 && DataLoader.Singleton().TransactionComission != 0){
            comissionFloat = 1;
        }
        int comissionAmount = (int)Math.ceil(comissionFloat);

        Amount = SenderPaid - comissionAmount;

        if (Sender.Balance >= SenderPaid){
            Sender.Balance -= SenderPaid;
            int transId = DataLoader.Singleton().getNextTransactionID();

            DataLoader.Singleton().Transactions.put(transId, this);
            Sender.Transactions.add(DataLoader.Singleton().Transactions.get(transId));
            Receiver.Transactions.add(DataLoader.Singleton().Transactions.get(transId));
            Receiver.Balance += Amount;

            return true;
        }

        return false;
    }


    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        if (Sender != null){
            b.write(BinaryUtils.Int2Bytes(Sender.ID));
        }else{
            b.write(BinaryUtils.Int2Bytes(-1));
        }
        if (Receiver != null){
            b.write(BinaryUtils.Int2Bytes(Receiver.ID));
        }else{
            b.write(BinaryUtils.Int2Bytes(-1));
        }


        b.write(BinaryUtils.Int2Bytes(Amount));
        b.write(BinaryUtils.Int2Bytes(SenderPaid));
        b.write((byte)Type.ordinal());
        b.write(BinaryUtils.WriteString(Description));

        return b.toByteArray();
    }

    @Nullable
    public static Transaction tryParse(@NonNull ByteBuffer b) throws IOException {

        try{
            Transaction t = new Transaction();

            int senderId = b.getInt();
            int receiverId = b.getInt();
            t.Sender = DataLoader.Singleton().GetPlayerById(senderId);
            t.Receiver = DataLoader.Singleton().GetPlayerById(receiverId);
            t.Amount = b.getInt();
            t.SenderPaid = b.getInt();
            t.Type = TransactionType.Get((int)b.get());
            t.Description = BinaryUtils.ReadString(b);

            return t;
        }catch(IOException ex){
            throw ex;
        }
    }

}
