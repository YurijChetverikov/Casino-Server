package com.pgp.casinoserver.net.packages;

import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class BytePackage extends Package{

    private ByteBuffer mData;

    private final String TAG = "Integer package";

    public BytePackage(byte o) {
        super(o);
        mData = ByteBuffer.allocate(1);
        mData.put(o);
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
            if (data.getClass() == byte.class){
                mData = ByteBuffer.allocate(1);
                mData.put((byte)data);
                setReady();
            }else{
                Log.e(TAG, "Invalid object type provided");
            }
        }
    }

    @Override
    public Byte parseResult(){
        super.parseResult();
        if (mData != null){
            if (mData.array().length == 1){
                try{
                    setReady();
                    return mData.get();
                }catch(Exception ex){

                }
            }else{
                Log.e(TAG, "Invalid data length");
            }
        }

        return 0;
    }

}
