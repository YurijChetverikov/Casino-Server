package com.pgp.casinoserver.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pgp.casinoserver.utils.BinaryUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String TAG = "Request class";
    private RequestHeader mHeader = null;
    private byte[] mPackage = null;
    private boolean mSuccess = false;


    // Этот конструктор предназначен только для входящих данных/запросов
    public Request(@NonNull InputStream input) throws IOException{
        int available = input.available(); // Кол-во прочтённых байт заголовка; чтобы их потом вычесть из available
        if (available > 0){
            try{
                mHeader = RequestHeader.parse(input);
                if (mHeader == null) {Log.e(TAG, "Invalid header"); return;}
                if (!mHeader.isReady()) {Log.e(TAG, "Invalid header"); return;}

                PackageType packType = (PackageType) mHeader.Values.get(RequestHeaderValues.PACKAGE_TYPE);
                RequestType reqType = (RequestType) mHeader.Values.get(RequestHeaderValues.REQUEST_TYPE);

                mPackage = new byte[available];
                input.read(mPackage, 0, available);

                switch (packType){
                    case PLAYER_FULL:
                        if (reqType == RequestType.GET){
                            // Здесь нам нужен только ID и пароль
                            if (mHeader.Values.containsKey(RequestHeaderValues.PLAYER_ID) &&
                                    mHeader.Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
                                mSuccess = true;
                            }else{
                                Log.e(TAG, "Header does not contain needed information");
                            }
                        }else{
                            Log.e(TAG, "Invalid request type");
                        }
                        break;
                    case PASSWORD:
                        if (reqType == RequestType.GET){
                            // Здесь нам нужен только ID и пароль
                            if (mHeader.Values.containsKey(RequestHeaderValues.PLAYER_ID) &&
                                    mHeader.Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
                                mSuccess = true;
                            }else{
                                Log.e(TAG, "Header does not contain needed information");
                            }
                        }else{
                            Log.e(TAG, "Invalid request type");
                        }
                        break;
                    case TRANSACTION_REQUEST:
                        if (reqType == RequestType.GET){
                            // Здесь нам нужен только ID и пароль
                            if (mHeader.Values.containsKey(RequestHeaderValues.PLAYER_ID) &&
                                    mHeader.Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
                                mSuccess = true;
                            }else{
                                Log.e(TAG, "Header does not contain needed information");
                            }
                        }else{
                            Log.e(TAG, "Invalid request type");
                        }
                        break;
                }



            }catch(IOException ex){
                Log.e(TAG, ex.toString());
            }
        }
    }


    // Этот конструктор предназначен только для исходящих данных/запросов
    public Request(@NonNull RequestHeader header, byte[] data){
        if (header == null) {Log.e(TAG, "Header in null"); return;}
        if (!header.isReady()) {Log.e(TAG, "Header is INVALID"); return;}
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

    public byte[] writeInArray() throws IOException {
        if (!mHeader.isReady()) {Log.e(TAG, "Header is INVALID"); return null;}

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(mHeader.write());

        if (mPackage != null){
            out.write(mPackage, 0, mPackage.length);
        }
        out.close();


        return out.toByteArray();
    }
}
