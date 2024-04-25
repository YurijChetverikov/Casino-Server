package com.pgp.casinoserver.net;

import android.content.Context;

import com.pgp.casinoserver.utils.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Transport {

    private final int mPort = 2109;
    private InetAddress mAddress = null;
    private Server mServer;

    private Context context;

    private final String TAG = "Transport layer";


    public Transport(Context context){
//        try {
//            mAddress = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
        this.context = context;
        mServer = new Server();
        mServer.start();
    }


    public void destroy(){
        if (mServer != null){
            mServer.tearDown();
        }
    }




    public class Server extends Thread{
        private boolean mRunning = false;
        private ServerSocket server;

        public Server() {

        }


        private void runServer() {
            mRunning = true;

            try {
                // создаём серверный сокет, он будет прослушивать порт на наличие запросов
                server = new ServerSocket(mPort);

                while (mRunning) {
                    Socket client = server.accept();
                    new RequestProcessor(client, context).start();
                }
            } catch (Exception e) {
                Logger.LogError(TAG, e);
            }
        }

        @Override public void run() {
            super.run();
            runServer();
        }

        protected void tearDown(){
            mRunning = false;
        }
    }


    public class Client extends Thread{


    }




}
