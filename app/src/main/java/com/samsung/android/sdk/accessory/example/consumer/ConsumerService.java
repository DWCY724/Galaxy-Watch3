package com.samsung.android.sdk.accessory.example.consumer;

import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ConsumerService extends SAAgentV2 {
    private static final String TAG = "Accessory";
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private ServiceConnection mConnectionHandler = null;
    private Context mContext;
    Handler mHandler = new Handler();

    public ConsumerService(Context context) {
        super(TAG, context,SASOCKET_CLASS);
        mContext = context;
        SA mAccessory = new SA();
        try{
            mAccessory.initialize(mContext);
        }catch (SsdkUnsupportedException e){
            if(processUnsupportedException(e)){
                return;
            }
        }catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            releaseAgent();
        }
    }
    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        if ((result == SAAgent.PEER_AGENT_FOUND) && (peerAgents != null)) {
            for(SAPeerAgent peerAgent:peerAgents)
                requestServiceConnection(peerAgent);
        } else if (result == SAAgent.FINDPEER_DEVICE_NOT_CONNECTED) {
            Toast.makeText(getApplicationContext(), "FINDPEER_DEVICE_NOT_CONNECTED", Toast.LENGTH_LONG).show();
            updateTextView("Disconnected");
            updateToggleButton(false);
        } else if (result == SAAgent.FINDPEER_SERVICE_NOT_FOUND) {
            Toast.makeText(getApplicationContext(), "FINDPEER_SERVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
            updateTextView("Disconnected");
            updateToggleButton(false);

        } else {
            Toast.makeText(getApplicationContext(), R.string.NoPeersFound, Toast.LENGTH_LONG).show();
        }
    }
    // Connection Requested
    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            acceptServiceConnectionRequest(peerAgent);
        }
    }
    // Connection Response: CONNECTION_SUCCESS、CONNECTION_ALREADY_EXIST、CONNECTION_DUPLICATE_REQUEST
    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        //連接成功
        if (result == SAAgent.CONNECTION_SUCCESS) {
            this.mConnectionHandler = (ServiceConnection) socket;
            updateTextView("Connected");
        } else if (result == SAAgent.CONNECTION_ALREADY_EXIST) {//已經連線上
            updateTextView("Connected");
            Toast.makeText(mContext, "CONNECTION_ALREADY_EXIST", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.CONNECTION_DUPLICATE_REQUEST) {//重複的連接請求
            Toast.makeText(mContext, "CONNECTION_DUPLICATE_REQUEST", Toast.LENGTH_LONG).show();
        } else {//連線失敗
            Toast.makeText(mContext, R.string.ConnectionFailure, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        super.onError(peerAgent, errorMessage, errorCode);
    }

    @Override
    protected void onPeerAgentsUpdated(SAPeerAgent[] peerAgents, int result) {
        final SAPeerAgent[] peers = peerAgents;
        final int status = result;
        mHandler.post(() -> {
            if (peers != null) {
                if (status == SAAgent.PEER_AGENT_AVAILABLE) {
                    Toast.makeText(getApplicationContext(), "PEER_AGENT_AVAILABLE", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "PEER_AGENT_UNAVAILABLE", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class ServiceConnection extends SASocket {
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {
            final String message = new String(data);
            addMessage("Received: ", message);
            // 添加上傳firebase的code
            //FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = database.getReference("Sleep");
            //DatabaseReference myRef = database.getReference("Sport");
            //myRef.setValue(message);
        }

        @Override
        protected void onServiceConnectionLost(int reason) {
            updateTextView("Disconnected");
            updateToggleButton(false);
            closeConnection();
        }
    }

    public void findPeers() {
        findPeerAgents();
    }

    // add message: 暫存資料 mailbox??
    private void addMessage(final String prefix, final String data) {
        final String strToUI = prefix.concat(data);
        mHandler.post(() -> MainActivity.addMessage(strToUI));
    }

    //send data: channel[0]
    public void sendData(final String data) {
        if (mConnectionHandler != null) {
            new Thread(() -> {
                try {
                    mConnectionHandler.send(getServiceChannelId(0), data.getBytes());
                    addMessage("Sent: ", data);
                } catch (IOException e) {
                    e.printStackTrace();
                    addMessage("Exception: ", e.getMessage());
                }
            }).start();
        } else {
            Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
        }
    }

    public boolean closeConnection() {
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
            return true;
        } else {
            return false;
        }
    }
    // Process Unsupport 原因
    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            releaseAgent();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

    private void updateTextView(final String str) {
        mHandler.post(() -> MainActivity.updateTextView(str));
    }
    private void updateToggleButton(final boolean enable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.updateToggleButton(enable);
            }
        });
    }
}
