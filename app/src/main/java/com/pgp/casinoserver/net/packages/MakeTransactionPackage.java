package com.pgp.casinoserver.net.packages;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pgp.casinoserver.core.Transaction;

import java.nio.ByteBuffer;

public class MakeTransactionPackage extends Package{

    private ByteBuffer mData;

    private final String TAG = "Make Transaction package";


    // Клиентский конструктор
    public MakeTransactionPackage(Transaction trans){
        super((Object) null);
        setData(trans);
    }


    @Override
    public void setData(byte[] data){
        super.setData(data);
        setNotReady();
        mData = ByteBuffer.wrap(data);
        setReady();
    }

    @Override
    public void setData(@NonNull Object data){
        if (data == null) return;
        if (data.getClass() == Transaction.class){
            super.setData(data);
            setNotReady();
            Transaction trans = (Transaction)data;
            try{
                setData(trans.getByteArray());
            }catch(Exception ex){
                Log.e(TAG, ex.toString());
            }
        }else{
            Log.e(TAG, "Wrong package type provided: " + data.getClass().toString());
        }
    }

    @Override
    public Transaction parseResult(){
        super.parseResult();
        setNotReady();
        if (mData != null){
            if (mData.array().length > 0){
                try{
                    return Transaction.tryParse(mData);
                }catch(Exception ex){
                    Log.e(TAG, ex.toString());
                }
            }
        }

        return null;
    }
}
