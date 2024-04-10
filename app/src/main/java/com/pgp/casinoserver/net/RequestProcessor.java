package com.pgp.casinoserver.net;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;

import java.io.IOException;
import java.net.Socket;

public class RequestProcessor extends Thread{

    private final String TAG = "RequestProcessor";

    private final Socket mClient;

    private static Request mError;


    public RequestProcessor(@NonNull Socket clientSocket){
        mClient = clientSocket;

        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, (byte)1);

        mError = new Request(h, null);
    }

    @NonNull
    private Request procced(@NonNull Request acceptedRequest){
        if (acceptedRequest == null) return mError;
        if (!acceptedRequest.isSuccess()) return mError;

        try{
            switch ((PackageType)acceptedRequest.getHeader().Values.get(RequestHeaderValues.PACKAGE_TYPE)){
                case PLAYER_FULL:
                    return proccess_PLAYERFULL(acceptedRequest);
                default:
                    mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, (byte)2);
                    return mError;
            }
        }catch(Exception ex){
            Log.e(TAG, ex.toString());
            return mError;
        }
    }




    private static Request proccess_PLAYERFULL(@NonNull Request req) throws IOException {
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, 0);
        h.Values.put(RequestHeaderValues.PACKAGE_TYPE, (PackageType)req.getHeader().Values.get(RequestHeaderValues.PACKAGE_TYPE));
        h.Values.put(RequestHeaderValues.REQUEST_TYPE, (RequestType)req.getHeader().Values.get(RequestHeaderValues.REQUEST_TYPE));

        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_ID)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, (byte)3);
            return mError;
        }
        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, (byte)4);
            return mError;
        }
        Player founded = DataLoader.Singleton().GetPlayerById((int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_ID));
        if (founded != null){
            if (founded.Password == (int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_PASSWORD)){
                return new Request(h, founded.getBytes());
            }else{
                mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, (byte)14);
                return mError;
            }

        }else{
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, (byte)13);
            return mError;
        }
    }


    private void writeResponse(Request response){

    }

    @Override
    public void start() {
        if (mClient == null){ writeResponse(mError); return; }

        try {
            Request req = new Request(mClient.getInputStream());
            if (!req.isSuccess()) {writeResponse(mError); return;}
            writeResponse(procced(req));
            return;
        }catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        writeResponse(mError);
    }
}
