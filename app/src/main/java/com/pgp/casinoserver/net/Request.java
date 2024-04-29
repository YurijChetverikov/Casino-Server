package com.pgp.casinoserver.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceActivity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pgp.casinoserver.utils.BinaryUtils;
import com.pgp.casinoserver.utils.PositionInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private static final String TAG = "Request class";
    private RequestHeader mHeader = null;
    private byte[] mPackage = null;
    private boolean mSuccess = false;


    private Request(){

    }

    @Nullable
    public static Request create(@NonNull byte[] inp) throws IOException{

        ByteBuffer input = ByteBuffer.wrap(inp);

        Request res = null;

        if (inp.length >= 3){
            try{
                res = new Request();
                res.mHeader = RequestHeader.parse(input);
                if (res.mHeader == null) {Log.e(TAG, "Invalid header"); return null;}
                if (!res.mHeader.check(true)) {Log.e(TAG, "Invalid header"); return null;}

                PackageType packType = (PackageType) res.mHeader.Values.get(RequestHeaderValues.PACKAGE_TYPE);
                RequestType reqType = (RequestType) res.mHeader.Values.get(RequestHeaderValues.REQUEST_TYPE);


                res.mPackage = new byte[input.remaining()];
                input.get(res.mPackage, 0, res.mPackage.length);

                switch (packType){
                    case PLAYER_FULL:
                    case PASSWORD:
                    case TRANSACTION_REQUEST:
                        if (reqType == RequestType.GET){
                            // Здесь нам нужен только ID и пароль
                            if (res.mHeader.Values.containsKey(RequestHeaderValues.PLAYER_ID) &&
                                    res.mHeader.Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
                                res.mSuccess = true;
                            }else{
                                Log.e(TAG, "Header does not contain needed information");
                            }
                        }else{
                            Log.e(TAG, "Invalid request type");
                        }
                        break;
                    default:
                        res.mSuccess = true;
                }



            }catch(IOException ex){
                Log.e(TAG, ex.toString());
            }
        }

        return res;
    }


    // Этот конструктор предназначен только для исходящих данных/запросов
    public Request(@NonNull RequestHeader header, byte[] data){
        if (header == null) {Log.e(TAG, "Header in null"); return;}

        // Проверяем header на соответствие
        if (!header.check(false)) {Log.e(TAG, "Invalid header"); return;}

        if (data == null) {Log.e(TAG, "Package is null"); /*return;*/}

        mHeader = header;
        mPackage = data;

        mSuccess = true;

    }


    public byte[] getPackage(){
        return mPackage;
    }

    public RequestHeader getHeader(){
        return mHeader;
    }

    public boolean isSuccess(){
        return mSuccess;
    }


    public byte[] writeInArray() throws Exception {
        if (!mHeader.check(false)) {Log.e(TAG, "Header is INVALID"); return null;}

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(mHeader.write());

        if (mPackage != null){
            out.write(mPackage, 0, mPackage.length);
        }


        byte[] buff = out.toByteArray();

        out.close();

        return buff;
    }

}
