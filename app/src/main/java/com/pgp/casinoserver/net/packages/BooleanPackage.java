package com.pgp.casinoserver.net.packages;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pgp.casinoserver.utils.BinaryUtils;

import java.nio.ByteBuffer;

public class BooleanPackage extends Package{

    private ByteBuffer mData;

    private final String TAG = "Boolean package";


    // Клиентский конструктор
    public BooleanPackage(boolean val){
        super((Object) null);
        setData(new byte[]{val == true ? (byte)1 : (byte)0});
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
        if (data.getClass() == boolean.class){
            super.setData(data);
            setNotReady();
            boolean val = (boolean)data;
            setData(new byte[]{val == true ? (byte)1 : (byte)0});

        }else{
            Log.e(TAG, "Wrong package type provided: " + data.getClass().toString());
        }
    }

    @Override
    public Boolean parseResult(){
        super.parseResult();
        setNotReady();
        if (mData != null){
            if (mData.array().length > 0){
                try{
                    return mData.get() == 0 ? false : true;
                }catch(Exception ex){
                    Log.e(TAG, ex.toString());
                }
            }
        }

        return null;
    }
}
