package com.pgp.casinoserver;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pgp.casinoserver.ui.MainActivity;

public class TestFragment extends Fragment  {

    NsdHelper mNsdHelper;
    //ChatConnection mConnection;
    private Handler mUpdateHandler;


    private View parentView;
    private Context context;
    private MainActivity activity;


    private TextView mStatusView;
    Button send;
    Button discovery;
    Button connect;
    Button register;




    public TestFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_test, container, false);

        activity = MainActivity.Singleton();
        context = parentView.getContext();

        mStatusView = parentView.findViewById(R.id.status);


        send = parentView.findViewById(R.id.send_btn);
        register = parentView.findViewById(R.id.advertise_btn);
        discovery = parentView.findViewById(R.id.discover_btn);
        connect = parentView.findViewById(R.id.connect_btn);

//        send.setOnClickListener(this::clickSend);
//        register.setOnClickListener(this::clickAdvertise);
//        discovery.setOnClickListener(this::clickDiscover);
//        connect.setOnClickListener(this::clickConnect);
//
//
//        mUpdateHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                String chatLine = msg.getData().getString("msg");
//                addChatLine(chatLine);
//            }
//        };
//
//        mNsdHelper = new NsdHelper(parentView.getContext());
//        mNsdHelper.initializeNsd();

        return parentView;
    }


//    @Override
//    public void onPause() {
//        if (mNsdHelper != null) {
//            mNsdHelper.stopDiscovery();
//        }
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mNsdHelper != null) {
//            mNsdHelper.discoverServices();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        mNsdHelper.tearDown();
//        mConnection.tearDown();
//        super.onDestroy();
//    }
//
//
//
//    public void clickAdvertise(View v) {
//        // Register service
//        if(mConnection.getLocalPort() > -1) {
//            mNsdHelper.registerService(mConnection.getLocalPort());
//        } else {
//            Log.d(NsdHelper.TAG, "ServerSocket isn't bound.");
//        }
//    }
//
//    public void clickDiscover(View v) {
//        mNsdHelper.discoverServices();
//    }
//
//    public void clickConnect(View v) {
//        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
//        if (service != null) {
//            Log.d(NsdHelper.TAG, "Connecting.");
//            mConnection.connectToServer(service.getHost(),
//                    service.getPort());
//        } else {
//            Log.d(NsdHelper.TAG, "No service to connect to!");
//        }
//    }
//
//    public void clickSend(View v) {
//        EditText messageView = (EditText) parentView.findViewById(R.id.chatInput);
//        if (messageView != null) {
//            String messageString = messageView.getText().toString();
//            if (!messageString.isEmpty()) {
//                mConnection.sendMessage(messageString);
//            }
//            messageView.setText("");
//        }
//    }
//
//    public void addChatLine(String line) {
//        mStatusView.append("\n" + line);
//    }

}