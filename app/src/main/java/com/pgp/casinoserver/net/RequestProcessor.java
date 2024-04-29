package com.pgp.casinoserver.net;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pgp.casinoserver.core.Game;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.core.Transaction;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.utils.BinaryUtils;
import com.pgp.casinoserver.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RequestProcessor extends Thread{

    private final String TAG = "RequestProcessor";

    private final Socket mClient;

    private static Request mError;

    private static Context context;


    public RequestProcessor(@NonNull Socket clientSocket, Context cont){
        mClient = clientSocket;
        context = cont;

        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.ERROR);

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
                case CASINO_LOGO:
                    return proccess_CASINOLOGO();
                case TRANSACTION_REQUEST:
                    return proccess_TRANSACTIONREQUEST(acceptedRequest);
                case GAMES:
                    return proccess_GAMES();
                case PLAYER:
                    return proccess_PLAYER(acceptedRequest);
                case TRANSACTION_COMISSION:
                    return proccess_TRANSACTIONCOMISSION();
                case TRANSACTIONS_HISTORY:
                    return proccess_TRANSACTIONSHISTORY(acceptedRequest);
                case CASINO_NAME:
                    return proccess_CASINONAME();

                default:
                    mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.INVALID_PACKAGE_TYPE);
                    return mError;
            }
        }catch(Exception ex){
            Logger.LogError(TAG, ex);
            return mError;
        }
    }

    private static Request proccess_CASINONAME() throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);
        return new Request(h, BinaryUtils.WriteString(DataLoader.Singleton().GetCasinoPlayer().Name));
    }

    private static Request proccess_TRANSACTIONSHISTORY(@NonNull Request req) throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);

        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_ID)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PLAYER_ID);
            return mError;
        }
        if (!req.getHeader().Values.containsKey(RequestHeaderValues.LAST_TRANSACTIONS_HISTORY_INDEX)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$LAST_TRANSACTIONS_HISTORY_INDEX);
            return mError;
        }
        Player founded = DataLoader.Singleton().GetPlayerById((int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_ID));
        if (founded != null){
            int startIndex = (int)req.getHeader().Values.get(RequestHeaderValues.LAST_TRANSACTIONS_HISTORY_INDEX) + 1;
            if (startIndex < -1){
                mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.ERROR);
                return mError;
            }
            if (founded.Transactions.size() - 1 >= startIndex){
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                startIndex = startIndex == -1 ? 0 : startIndex;
                b.write(BinaryUtils.Int2Bytes(founded.Transactions.size() - startIndex));
                for (int i = startIndex; i < founded.Transactions.size(); i++){
                    b.write(founded.Transactions.get(i).getByteArray());
                }

                return new Request(h, b.toByteArray());
            }else{
                mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.ERROR);
                return mError;
            }
        }else{
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_NOT_FOUND$PLAYER_WITH_ID);
            return mError;
        }
    }

    private static Request proccess_TRANSACTIONCOMISSION() throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);
        return new Request(h, new byte[]{DataLoader.Singleton().TransactionComission});
    }


    private static Request proccess_PLAYER(@NonNull Request req) throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);

        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_ID)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PLAYER_ID);
            return mError;
        }

        Player founded = DataLoader.Singleton().GetPlayerById((int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_ID));
        if (founded != null){
            return new Request(h, BinaryUtils.WriteString(founded.Name));
        }else{
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_NOT_FOUND$PLAYER_WITH_ID);
            return mError;
        }
    }

    private static Request proccess_GAMES() throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write((byte)DataLoader.Singleton().Games.size());

        for (Game g: DataLoader.Singleton().Games){
            outputStream.write(g.ID);
            outputStream.write(BinaryUtils.WriteString(g.Name));
        }

        return new Request(h, outputStream.toByteArray());
    }


    @NonNull
    private static Request proccess_TRANSACTIONREQUEST(@NonNull Request req) throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);

        if (req.getPackage() == null) {
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PACKAGE);
            return mError;
        }

        Transaction t = Transaction.tryParse(ByteBuffer.wrap(req.getPackage()));

        if (t == null) {
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PACKAGE);
            return mError;
        }
        if (!t.check()){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_NOT_FOUND$PLAYER_WITH_ID);
            return mError;
        }

        if (t.apply()){
            while(DataLoader.Singleton().isBusy()){
                // Ждём, пока дата лоадер освободится
            }
            DataLoader.Singleton().WriteBigDataCache(context);
            DataLoader.Singleton().WriteTableCache(context);
            return new Request(h, t.getByteArray());
        }

        mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.ERROR);
        return mError;
    }


    @NonNull
    private static Request proccess_CASINOLOGO() throws IOException{
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);
        h.Values.put(RequestHeaderValues.PACKAGE_TYPE, PackageType.CASINO_LOGO);

        Request res = null;

        if (DataLoader.Singleton().CompressedBitmap != null){
            res = new Request(h, DataLoader.Singleton().CompressedBitmap);
        }else{
            res = new Request(h, new byte[0]);
        }

        return res;
    }

    private static Request proccess_PLAYERFULL(@NonNull Request req) throws IOException {
        RequestHeader h = new RequestHeader();
        h.Values.put(RequestHeaderValues.ERROR_CODE, RequestErrorCode.GOOD);

        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_ID)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PLAYER_ID);
            return mError;
        }
        if (!req.getHeader().Values.containsKey(RequestHeaderValues.PLAYER_PASSWORD)){
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_MISSING$PLAYER_PASSWORD);
            return mError;
        }
        Player founded = DataLoader.Singleton().GetPlayerById((int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_ID));
        if (founded != null){
            if (founded.Password == (int)req.getHeader().Values.get(RequestHeaderValues.PLAYER_PASSWORD)){
                return new Request(h, founded.getBytes());
            }else{
                mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_NOT_FOUND$PLAYER_WITH_PASSWORD);
                return mError;
            }

        }else{
            mError.getHeader().Values.replace(RequestHeaderValues.ERROR_CODE, RequestErrorCode.DATA_NOT_FOUND$PLAYER_WITH_ID);
            return mError;
        }
    }


    private void writeResponse(Request response) {
        if (response == null) {return;}

        try{
            byte[] buffer = response.writeInArray();
            mClient.getOutputStream().write(BinaryUtils.Int2Bytes(buffer.length));
            mClient.getOutputStream().write(buffer);
            mClient.shutdownOutput();
            mClient.close();
        }catch(Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void start() {
        if (mClient == null){ writeResponse(mError); return; }

        try {
            byte[] lengthBuffer = new byte[4];

            if (mClient.getInputStream().read(lengthBuffer) == -1){
                Log.w(TAG, "Error while reading data length!");
                writeResponse(mError);
                return;
            }

            int dataLength = BinaryUtils.Bytes2Int(lengthBuffer);
            byte[] dataBuffer = new byte[dataLength];

            DataInputStream ds = new DataInputStream(mClient.getInputStream());
            ds.readFully(dataBuffer, 0, dataBuffer.length);

            Request req = Request.create(dataBuffer);
            if (req == null) {writeResponse(mError); return;}
            if (!req.isSuccess()) {writeResponse(mError); return;}
            writeResponse(procced(req));
            return;
        }catch (IOException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
            writeResponse(mError);
        }

        writeResponse(mError);
    }
}
