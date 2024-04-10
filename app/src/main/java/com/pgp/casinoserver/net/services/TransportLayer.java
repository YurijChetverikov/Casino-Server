package com.pgp.casinoserver.net.services;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pgp.casinoserver.core.Player;
import com.pgp.casinoserver.loaders.DataLoader;
import com.pgp.casinoserver.net.Request;
import com.pgp.casinoserver.net.RequestManager;
import com.pgp.casinoserver.net.packages.Package;
import com.pgp.casinoserver.net.packages.PlayerPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TransportLayer {


    private final String TAG = "TRANSPORT_LAYER";
    private final String SERVER_NAME = "PGP_CASINO_SERVER"; //idk what this should be.  doc's don't say.
    private final String SERVER_ID = "pgp.casinoserver";
    boolean mIsAdvertising = false;

    //String ConnectedEndPointId;

    private Context mContext;
    private Strategy mStrategy = Strategy.P2P_STAR; // Для сервера star надо



    public TransportLayer(Context context) {
        mContext = context;
        startAdvertising();
    }


    /**
     * Callbacks for connections to other devices.  These call backs are used when a connection is initiated
     * and connection, and disconnect.
     * <p>
     * we auto accept any connection.  We with another callback that allows us to read the data.
     */
    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                    Log.i(TAG,"Connection Initiated :" + endpointId + " Name is " + connectionInfo.getEndpointName());
                    // Automatically accept the connection on both sides.
                    // setups the callbacks to read data from the other connection.

                    if (connectionInfo.getEndpointName() != "PGP_CASINO_CLIENT"){
                        Log.e(TAG, "end point name != PGP_CASINO_CLIENT");
                        Nearby.getConnectionsClient(mContext).rejectConnection(endpointId);
                        return;
                    }

                    ConnectionsClient client = Nearby.getConnectionsClient(mContext);

                    client.acceptConnection(endpointId, new PayloadCallback() {
                        @Override
                        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                            if (payload.getType() == Payload.Type.BYTES) {
                                RequestManager mgr = new RequestManager(client, endpointId);
                                mgr.start();
                                Log.i(TAG,"---DataRecieved---");
                                // Получили посылку - начинаем работать по этому вопросу
                            }else if (payload.getType() == Payload.Type.FILE){
                                Log.i(TAG,"We got a file.  not handled");
                            }
                            else if (payload.getType() == Payload.Type.STREAM){
                                //payload.asStream().asInputStream()
                                Log.i(TAG,"We got a stream, not handled");
                            }
                        }

                        @Override
                        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

                        }
                    });
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
                    Log.i(TAG,"Connection accept :" + endpointId + " result is " + result.toString());

                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            //ConnectedEndPointId = endpointId;
                            //if we don't then more can be added to conversation, when an List<string> of endpointIds to send to, instead a string.
                            // ... .add(endpointId);
                            //stopAdvertising();  //and comment this out to allow more then one connection.
                            Log.i(TAG,"Status ok");
                            //send("Hi from Advertiser");
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            Log.i(TAG,"Status rejected.  :(");
                            // The connection was rejected by one or both sides.
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            Log.i(TAG,"Status error.");
                            // The connection broke before it was able to be accepted.
                            break;
                    }
                }

                @Override
                public void onDisconnected(@NonNull String endpointId) {
                    Log.i(TAG,"Connection disconnected :" + endpointId);
                    //ConnectedEndPointId = "";  //need a remove if using a list.
                }
            };

    /**
     * Start advertising the nearby.  It sets the callback from above with what to once we get a connection
     * request.
     */
    private void startAdvertising() {

        Nearby.getConnectionsClient(mContext)
                .startAdvertising(
                        SERVER_NAME,    //human readable name for the endpoint.
                        SERVER_ID,  //unique identifier for advertise endpoints
                        mConnectionLifecycleCallback,  //callback notified when remote endpoints request a connection to this endpoint.
                        new AdvertisingOptions.Builder().setStrategy(mStrategy).build()
                )
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                mIsAdvertising = true;
                                Log.i(TAG,"we're advertising!");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mIsAdvertising = false;
                                // We were unable to start advertising.
                                Log.i(TAG,"we're failed to advertise");
                                e.printStackTrace();
                            }
                        });
    }

    /**
     * turn off advertising.  Note, you can not add success and failure listeners.
     */
    public void stopAdvertising() {
        mIsAdvertising = false;
        Nearby.getConnectionsClient(mContext).stopAdvertising();
        Log.i(TAG,"Advertising stopped.");
    }


    /**
     * Sends a {@link Payload} to all currently connected endpoints.
     */
//    protected void send(byte[] data) {
//
//        //basic error checking
//        if (ConnectedEndPointId.compareTo("") == 0)   //empty string, no connection
//            return;
//
//        Payload payload = Payload.fromBytes(data.getBytes());
//
//        // sendPayload (List<String> endpointIds, Payload payload)  if more then one connection allowed.
//        Nearby.getConnectionsClient(mContext).
//                sendPayload(ConnectedEndPointId,  //end point to end to
//                        payload)   //the actual payload of data to send.
//                .addOnSuccessListener(new OnSuccessListener<Void>() {  //don't know if need this one.
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i(TAG,"Message send successfully.");
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i(TAG,"Message send completed.");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG,"Message send failed.");
//                        e.printStackTrace();
//                    }
//                });
//    }

    
    public void onStop() {
        stopAdvertising();
    }
}
