package com.pgp.casinoserver.core;

import androidx.annotation.NonNull;

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


    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        b.write(BinaryUtils.Int2Bytes(Sender.ID));
        b.write(BinaryUtils.Int2Bytes(Receiver.ID));
        b.write(BinaryUtils.Int2Bytes(Amount));
        b.write(BinaryUtils.Int2Bytes(SenderPaid));
        b.write((byte)Type.ordinal());
        b.write(BinaryUtils.WriteString(Description));

        return b.toByteArray();
    }

    @NonNull
    public static Transaction tryParse(@NonNull ByteBuffer b) throws IOException {
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
    }

}
