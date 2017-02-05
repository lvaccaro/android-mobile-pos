package vaccarostudio.com.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vaccarostudio.com.verifone.ConnectorIntent;
import vaccarostudio.com.verifone.R;
import vaccarostudio.com.verifone.SerialIO;

public class TestActivity extends AppCompatActivity {

    EditText editText;
    EditText etRecv;
    TextView tvDevice;
    TextView tvChannel;

    byte []start={
            0x02 ,0x00 ,0x01 ,0x01 ,0x03 ,0x61 ,0x6e ,0x64 ,0x72 ,0x6f ,0x69 ,0x64 ,0x2d
            ,0x74 ,0x65 ,0x73 ,0x74 ,0x2d ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30,0x30
            ,0x30 ,0x30 ,0x00 ,0x00 ,0x00 ,0x14 , (byte) 0x9f,(byte) 0x86 ,0x10 ,0x10 ,0x30 ,0x30 ,0x30,0x30
            ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x31 ,0x03 ,(byte) 0xb2 ,0x2f ,(byte) 0xb7 ,(byte) 0xa4
    };
    byte []ack= {
            0x02 ,0x00 ,0x06 ,0x01 ,0x02 ,0x61 ,0x6e ,0x64 ,0x72 ,0x6f,0x69 ,0x64 ,0x2d ,0x74 ,0x65 ,0x73
            ,0x74 ,0x2d ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x30 ,0x00 ,0x00 ,0x00 ,0x00 ,0x03 ,0x48 ,0x26 ,0x09 , (byte)0x9b
    };
    byte []state= {
            0x02 ,0x00 ,0x0e ,0x01 ,0x03 ,0x61 ,0x6e ,0x64 ,0x72 ,0x6f ,0x69 ,0x64 ,0x2d ,0x74 ,0x65 ,0x73 ,0x74 ,0x2d ,0x30 ,0x30 ,0x30 ,0x35 ,0x30 ,0x30 ,0x37 ,0x32 ,0x36 ,0x34 ,0x36 ,0x00 ,0x00 ,0x00 ,0x05 ,(byte)0x9f ,(byte)0x86 ,0x25 ,0x01 ,(byte)0x80 ,0x03 ,0x29 ,(byte)0xd5 ,(byte)0xed,0x11

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(EditText) findViewById(R.id.editText);
        etRecv=(EditText) findViewById(R.id.editText2);
        tvDevice=(TextView) findViewById(R.id.tvDevice);
        tvChannel=(TextView) findViewById(R.id.tvChannel);
        tvDevice.setText("NOT ATTACHED");
        tvChannel.setText("CLOSE");

        Settings.context=getApplicationContext();
        Settings.Sdk.get().setDevice(Settings.Sdk.PREF_DEVICE_USB);
        SerialIO.handlerCallback=handlerCallback;




        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncOpen asyncOpen = new AsyncOpen();
                asyncOpen.execute();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncClose asyncClose = new AsyncClose();
                asyncClose.execute();
            }
        });

        findViewById(R.id.btnAttach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectorIntent.get().attach();
            }
        });

        findViewById(R.id.btnDetach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectorIntent.get().detach();
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncSend asyncSend = new AsyncSend();
                asyncSend.buffer=start;
                asyncSend.execute();
                outLog(asyncSend.buffer);
            }
        });

        findViewById(R.id.btnAck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncSend asyncSend = new AsyncSend();
                asyncSend.buffer=ack;
                asyncSend.execute();
                outLog(asyncSend.buffer);
            }
        });

        findViewById(R.id.btnState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncSend asyncSend = new AsyncSend();
                asyncSend.buffer=state;
                asyncSend.execute();
                outLog(asyncSend.buffer);
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncSend asyncSend = new AsyncSend();
                asyncSend.buffer=editText.getText().toString().getBytes();
                asyncSend.execute();
                outLog(asyncSend.buffer);

            }
        });

    }

    void outLog(byte[] data){
        String output="OUT:";
        for (int i=0;i<data.length;i++)
            output+=String.format("%02x",data[i]);
        output+="\n";
        Log.d("SERIAL",output);
        etRecv.append(output);

    }

    Handler handlerCallback = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("string");
            etRecv.append(string);
        }
    };


    public BroadcastReceiver mReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(ConnectorIntent.ACTION_OPEN)) {
                tvDevice.setText("ATTACHED");
                Overlay.hide();
                if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_AUDIO)) {
                    Toast.makeText(TestActivity.this, "ATTACH", Toast.LENGTH_LONG).show();
                } else if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
                    Toast.makeText(TestActivity.this, "ATTACH", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(TestActivity.this, "ATTACH", Toast.LENGTH_LONG).show();
                    ;//attach();
                    ;//startState();
                }
            } else if (action.equals(ConnectorIntent.ACTION_CLOSE)) {
                tvDevice.setText("DETACH");
                Log.d("ACTION", "ACTION CLOSE");
                Overlay.hide();
                Toast.makeText(TestActivity.this, "DETACH", Toast.LENGTH_LONG).show();

                ;//detach();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register event on current Connector
        IntentFilter intentFilter = new IntentFilter(ConnectorIntent.ACTION_OPEN);
        intentFilter.addAction(ConnectorIntent.ACTION_CLOSE);
        intentFilter.addAction(ConnectorIntent.ACTION_SYNCHRONIZE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(mReceiver, intentFilter);
        ConnectorIntent.get().onResume(this);


    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    public class AsyncOpen extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                SerialIO.Open();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final Boolean res) {
            Toast.makeText(TestActivity.this,"OPEN",Toast.LENGTH_LONG).show();
            if(res==true)
                tvChannel.setText("OPEN");
        }
    }

    public class AsyncClose extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                SerialIO.Close();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final Boolean res) {
            Toast.makeText(TestActivity.this,"CLOSE",Toast.LENGTH_LONG).show();
            if(res==true)
                tvChannel.setText("CLOSE");
        }
    }

    public class AsyncSend extends AsyncTask<Void, Void, Boolean> {
        public byte []buffer;
        protected Boolean doInBackground(Void... voids) {
            try {
                SerialIO.Send(buffer);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final Boolean res) {
            Toast.makeText(TestActivity.this,"SEND",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_test)
            startActivity(new Intent(this,TestActivity.class));
        else if(id==R.id.action_buetooth)
            startActivity(new Intent(this,BluetoothActivity.class));

        return super.onOptionsItemSelected(item);
    }


}
