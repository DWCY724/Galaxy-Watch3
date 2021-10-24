package com.samsung.android.sdk.accessory.example.consumer;

import android.os.Bundle;
import com.samsung.android.sdk.accessory.SAAgentV2;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.samsung.android.sdk.accessory.*;


public class MainActivity extends Activity {
    //SAAgentV2 object
    private ConsumerService mConsumerService = null;
    //Error時用的TAG
    private static final String TAG = "ConsumerService";
    //Toggle button:可自行切換狀態(Connect/Disconnect)
    private static ToggleButton buttonConnect;
    //class自行定義的MessageAdapter
    private static MainActivity.MessageAdapter mMessageAdapter;
    //List:(id)send_result
    private ListView mMessageListView;
    //Text
    private static TextView mTextView;//id: Terminal
    private static TextView hrSent;//id:hrSent
    private static TextView pedoSent;//id:PedoSent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize
        InitializeAccessory();
    }
    //Click button
    public void OnClick(View v){
        switch (v.getId()){
            case R.id.buttonConnect:{
                if(mConsumerService != null){
                    //當按鈕狀態為選取時
                    if(buttonConnect.isChecked()){
                        buttonConnect.setChecked(false);
                        Log.e("mConsumerService", String.valueOf(mConsumerService));
                        Log.e("","Connect1?");
                        mConsumerService.findPeers();//找Tizen
                        Log.e("mConsumerService", String.valueOf(mConsumerService));
                        Log.e("","Connect1?");
                    }
                    //當按鈕狀態為未選取時，並且沒有關掉連線
                    else{
                        if(mConsumerService.closeConnection() == false){
                            updateTextView("Disconnected");
                            Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                            mMessageAdapter.clear();
                            Log.e("","Disonnect?");
                        }
                    }

                }
                break;
            }
            case R.id.buttonSend:{
                if(mConsumerService != null){
                    mConsumerService.sendData("Hello Accessory!");
                } else {
                    Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
        }
    }
    // (SAAgentV2) Accessory Initialize success or not
    private SAAgentV2.RequestAgentCallback mAgentCallback1 = new SAAgentV2.RequestAgentCallback() {
        @Override
        public void onAgentAvailable(SAAgentV2 saAgentV2) {
            mConsumerService = (ConsumerService) saAgentV2;
            Log.e(TAG,"Accessory Initialize success");
        }

        @Override
        public void onError(int errorCode, String massage) {
            Log.e(TAG, "Agent initialization error: " + errorCode + ". ErrorMsg: " + massage);
        }
    };
    //Initialize
    private void InitializeAccessory(){
        mMessageListView = (ListView) findViewById(R.id.lvMessage);
        mMessageAdapter = new MainActivity.MessageAdapter();
        mMessageListView.setAdapter(mMessageAdapter);
        buttonConnect = ((ToggleButton) findViewById(R.id.buttonConnect));
        mTextView = (TextView) findViewById(R.id.tvStatus);
        hrSent = (TextView) findViewById(R.id.hrSent);
        pedoSent = (TextView) findViewById(R.id.PedoSent);

        updateTextView("Disconnected");
        SAAgentV2.requestAgent(getApplicationContext(), ConsumerService.class.getName(), mAgentCallback1);
    }
    //Destroy Accesssory
    @Override
    protected void onDestroy() {
        // Clean up connections
        destroyAccessory();
        super.onDestroy();
    }
    private void destroyAccessory() {
        if (mConsumerService != null) {
            if (!mConsumerService.closeConnection()) {
                updateTextView("Disconnected");
                mMessageAdapter.clear();
            }
            mConsumerService.releaseAgent();
            mConsumerService = null;
        }
    }
    // addMessage: add Msg to viewList
    public static void addMessage(String data){
        mMessageAdapter.addMessage(new MainActivity.Message(data));
        Log.e("data: ", data);
        //hrSent.setText("unknownType");
        pedoSent.setText(data);
    }
    //輔助ConsumerService
    public static void updateToggleButton(boolean enable) {
        buttonConnect.setChecked(enable);
    }
    public static void updateTextView(final String str) {
        mTextView.setText(str);
    }
    // Message Adapter
    private class MessageAdapter extends BaseAdapter {
        private static final int MAX_MESSAGES_TO_DISPLAY = 20;
        private List<MainActivity.Message> mMessages;//mailbox??

        public MessageAdapter() {
            mMessages = Collections.synchronizedList(new ArrayList<MainActivity.Message>());
        }

        void addMessage(final MainActivity.Message msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMessages.size() == MAX_MESSAGES_TO_DISPLAY) {
                        mMessages.remove(0);
                        mMessages.add(msg);
                        //eggShow.setText(msg.data);//++ //Received: 15:47:57:636 -> Heart Rate: -3
                    } else {
                        mMessages.add(msg);//msg: com.samsung.android.sdk.accessory.example.consumer.AccessoryActivity$Message@4392b9a//msg: ...@c6e6766
                        //eggShow.setText(msg.data);//++//資料量超過手機頁面大小時才有顯示新增的最後一筆資料
                    }
                    notifyDataSetChanged();
                    mMessageListView.setSelection(getCount() - 1);//add to the last one of viewList
                }
            });
        }

        void clear() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessages.clear();
                    notifyDataSetChanged();
                }
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View messageRecordView = null;
            if (inflator != null) {
                messageRecordView = inflator.inflate(R.layout.message, null);
                TextView tvData = (TextView) messageRecordView.findViewById(R.id.tvData);
                MainActivity.Message message = (MainActivity.Message) getItem(position);
                tvData.setText(message.data);
            }
            return messageRecordView;
        }
    }

    private static final class Message {
        String data;

        public Message(String data) {
            super();
            this.data = data;//Received: 15:47:57:636 -> Heart Rate: -3
            String a;
        }
    }

}