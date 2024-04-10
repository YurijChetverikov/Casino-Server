package com.pgp.casinoserver.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.core.Transaction;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.net.packages.BooleanPackage;
import com.pgp.casinoserver.net.packages.MakeTransactionPackage;
import com.pgp.casinoserver.net.packages.Package;
import com.pgp.casinoserver.net.packages.PlayerPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestManager extends Thread{

    private Socket mClient;
    private ConnectionsClient mPayloadClient;
    private String mConnectionEndpoint;
    private boolean mRunning = false;
    private OutputStream mOut;

    private final Request mBadPlayerCallback = new Request(RequestHeader.getBadResponse(), null);


    private static final String TAG = "Request Manager";

    public RequestManager(Socket client){
        Log.i(TAG, "New client accepted.. Processing");
        mClient = client;
    }

    public RequestManager(ConnectionsClient client, String endpoint){
        Log.i(TAG, "New client accepted.. Processing");
        mPayloadClient = client;
        mConnectionEndpoint = endpoint;
    }



    // Сервер сам никогда ничё не отправляет, только отвечает на запросы. Так что логика такая -
    // всегда то, что нам прислали - запрос на что-то (например на обновление ленты новостей)
    private void proccedRequest(@NonNull Request r) throws Exception {
        if (!r.isSuccess()) writeResponse(mBadPlayerCallback);
        PackageType packType = (PackageType)r.getHeader().Values.get(RequestHeaderValues.PACKAGE_TYPE);
        RequestType reqType = (RequestType)r.getHeader().Values.get(RequestHeaderValues.REQUEST_TYPE);
        if (packType == PackageType.PLAYER_FULL){
            // Если у нас спросили игрока
            int id = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_ID);
            int pass = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_PASSWORD);

            Player founded = DataLoader.Singleton().GetPlayerById(id);

            if (founded != null){
                if (founded.Password == pass){
                    writeResponse(new Request(RequestHeader.getGoodResponse(PackageType.PLAYER_FULL), new PlayerPackage(founded)));
                }else{
                    writeResponse(mBadPlayerCallback);
                }
            }else{
                writeResponse(mBadPlayerCallback);
            }
        }else if (packType == PackageType.PASSWORD){
            // Тут нам на вход ничего не приходит, мы должны отослать boolean - правильный пароль или нет

            int id = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_ID);
            int pass = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_PASSWORD);

            Player founded = DataLoader.Singleton().GetPlayerById(id);

            if (founded != null){
                if (founded.Password == pass){
                    writeResponse(new Request(RequestHeader.getGoodResponse(PackageType.PASSWORD), new BooleanPackage(true)));
                }else{
                    writeResponse(new Request(RequestHeader.getGoodResponse(PackageType.PASSWORD), new BooleanPackage(false)));
                }
            }else{
                writeResponse(mBadPlayerCallback);
            }
        }else if (packType == PackageType.TRANSACTION_REQUEST){
            // Тут нам на вход приходит пароль, id и вся транзакция в package'е

            int id = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_ID);
            int pass = (int)r.getHeader().Values.get(RequestHeaderValues.PLAYER_PASSWORD);

            Player founded = DataLoader.Singleton().GetPlayerById(id);

            RequestHeader badRequest = RequestHeader.getBadResponse();

            if (founded != null){
                if (founded.Password == pass){
                    Transaction trans = ((MakeTransactionPackage)r.getPackage()).parseResult();
                    if (founded.Balance >= trans.SenderPaid){
                        // Денег хватило
                        Player receiver = DataLoader.Singleton().GetPlayerById(trans.);
                    }else{
                        // Денег не хватило
                        badRequest.Values.put(RequestHeaderValues.REQUEST_CODE, 2);
                        writeResponse(new Request(badRequest, new MakeTransactionPackage(null)));
                    }
                    writeResponse(new Request(RequestHeader.getGoodResponse(PackageType.TRANSACTION_REQUEST), new BooleanPackage(true)));
                }else{
                    badRequest.Values.put(RequestHeaderValues.REQUEST_CODE, 1);
                    writeResponse(new Request(badRequest, new MakeTransactionPackage(null)));
                }
            }else{
                badRequest.Values.put(RequestHeaderValues.REQUEST_CODE, 0);
                writeResponse(new Request(badRequest, new MakeTransactionPackage(null)));
            }
        }
    }

    private void writeResponse(@NonNull Request response) throws Exception{
        if (mClient != null){
            mClient.getOutputStream().write(response.writeInArray());
        }else if (mPayloadClient != null){
            Payload payload = Payload.fromBytes(response.writeInArray());
            mPayloadClient.sendPayload(mConnectionEndpoint, payload);
            mPayloadClient.disconnectFromEndpoint(mConnectionEndpoint);
        }

    }


    @Override public void run() {
        super.run();
        try {
            // в бесконечном цикле ждём сообщения от клиента и смотрим, что там
            //while (mRunning) {

                if (mClient.getInputStream() != null){
                    int available = mClient.getInputStream().available();
                    if (available >= 5){
                        Request r = new Request(mClient.getInputStream(), available);
                        if (r.isSuccess()){
                            proccedRequest(r);
                        }
                        mRunning = false;
                    }

                }else{
                    Log.e(TAG, "Client's input stream is null");
                }
            //}

            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ConnectionsClient getClient(){
        return mPayloadClient;
    }


    public void close() {
        mRunning = false;



        try {
            if (mOut != null) {
                mOut.flush();
                mOut.close();
            }
            if (mClient != null){
                mClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mClient = null;
    }






}
