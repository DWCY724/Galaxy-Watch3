package com.samsung.android.sdk.accessory.example.consumer;
/*
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.samsung.android.sdk.accessory.SAAgentV2;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.samsung.android.sdk.accessory.*;


public class MainActivity extends Activity {

    //Togglebutton
    private static ToggleButton buttonConnect;

    //private boolean Is_Bound = false;//監聽
    private ConsumerService consumerservice = null;//傳送服務
    //addmessage
    private ListView mMessageListView;
    private static MessageAdapter mMessageAdapter;
    @SuppressLint("StaticFieldLeak")
    private static TextView output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //super: AppCompatActivity
        setContentView(R.layout.activity_main);
        // Bind service
        //Is_Bound = bindService(new Intent(MainActivity.this,ConsumerService.class),Is_connect, Context.BIND_AUTO_CREATE);
        // Button click
        Button connect = (Button)findViewById(R.id.connection);
        Button send = (Button)findViewById(R.id.send);
        Button disconnect = (Button)findViewById(R.id.disconnection);
        connect.setOnClickListener(connect_request);
        send.setOnClickListener(send_request);
        disconnect.setOnClickListener(disconnection);
        // show text
        output = (TextView)findViewById(R.id.Terminal);
        //add message
        mMessageListView= (ListView) findViewById(R.id.send_result);
        mMessageAdapter = new MessageAdapter();
        mMessageListView.setAdapter(mMessageAdapter);
    }
    //Initialize

    // Connect
    private final View.OnClickListener connect_request = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            //connect to Tizen之後該做的事
            output.setText("Connect to Tizen");
            if(consumerservice != null){
                //找Tizen(同伴)
                output.setText("Finding Tizen...");
                consumerservice.findPeers();

            }
        }
    };
    // Send
    private final View.OnClickListener send_request = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            //send request
            // Send to Tizen: HRM、Pedometer、SleepStatus
            //EditText Send_type = (EditText)findViewById(R.id.Send);
            //if(consumerservice.sendData("SleepStatus")){
            //   output.setText("Send to Tizen");
            // }
        }
    };
    // Disconnect
    private final View.OnClickListener disconnection = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            //disconnect to Tizen
            if(Is_Bound == true && consumerservice != null){
                if(!consumerservice.closeConnection()){
                    output.setText("Disconnect to Tizen");
                }
            }
            if(Is_Bound == true){
                unbindService(Is_connect);
                Is_Bound = false;
            }
        }
    };
    // Bind service
    //Is_Bound = bindService(new Intent(MainActivity.this,ConsumerService.class),Is_connect, Context.BIND_AUTO_CREATE);
    /*
    private final ServiceConnection Is_connect = new ServiceConnection() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            consumerservice = ((ConsumerService.LocalBinder)service).getService();
            output.setText("On Service Connected");
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onServiceDisconnected(ComponentName name) {
            consumerservice = null;
            Is_Bound = false;
            output.setText("On Service Disconnected");
        }
    };*/
/*    public static void updateTextView(final String str) {
        output.setText(str);
    }

    //　Update button
    public static void updateToggleButton(boolean enable) {
        buttonConnect.setChecked(enable);
    }

    // Add message 以下照抄
    public static void addMessage(String data) {
        mMessageAdapter.addMessage(new Message(data));
    }
    private class MessageAdapter extends BaseAdapter {
        private static final int MAX_MESSAGES_TO_DISPLAY = 20;
        private final List<Message> mMessages;

        public MessageAdapter() {
            mMessages = Collections.synchronizedList(new ArrayList<>());
        }

        void addMessage(final Message msg) {
            runOnUiThread(() -> {
                if (mMessages.size() == MAX_MESSAGES_TO_DISPLAY) {
                    mMessages.remove(0);
                }
                mMessages.add(msg);
                notifyDataSetChanged();
                mMessageListView.setSelection(getCount() - 1);
            });
        }

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View messageRecordView = null;
            if (inflator != null) {
                messageRecordView = inflator.inflate(R.layout.message, null);
                TextView tvData = (TextView) messageRecordView.findViewById(R.id.Terminal);
                Message message = (Message) getItem(position);
                tvData.setText(message.data);
            }
            return messageRecordView;
        }
    }

    private static final class Message {
        String data;

        public Message(String data) {
            super();
            this.data = data;
        }
    }
}

 */