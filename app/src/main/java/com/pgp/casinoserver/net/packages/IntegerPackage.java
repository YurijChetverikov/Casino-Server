package com.pgp.casinoserver.net.packages;

import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class IntegerPackage extends Package{
    private ByteBuffer mData;

    private final String TAG = "Integer package";

    public IntegerPackage(int o) {
        super(o);
        mData = ByteBuffer.allocate(4);
        mData.putInt(o);
        setReady();
    }


    @Override
    public void setData(byte[] data){
        super.setData(data);
        mData = ByteBuffer.wrap(data);
        setReady();
    }


    @Override
    public void setData(@NonNull Object data){
        if (data != null){
            if (data.getClass() == int.class){
                mData = ByteBuffer.allocate(4);
                mData.putInt((int)data);
                setReady();
            }else{
                Log.e(TAG, "Invalid object type provided");
            }
        }
    }

    @Override
    public Integer parseResult(){
        super.parseResult();
        if (mData != null){
            if (mData.array().length == 4){
                try{
                    setReady();
                    return mData.getInt();
                }catch(Exception ex){

                }
            }else{
                Log.e(TAG, "Invalid data length");
            }
        }

        return Integer.MIN_VALUE;
    }
}
