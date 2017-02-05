package vaccarostudio.com.gui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import vaccarostudio.com.verifone.ConnectorIntent;
import vaccarostudio.com.verifone.MComm;
import vaccarostudio.com.verifone.R;

public class BluetoothActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_bluetooth);
        editText=(EditText) findViewById(R.id.editText);
        etRecv=(EditText) findViewById(R.id.editText2);
        tvDevice=(TextView) findViewById(R.id.tvDevice);
        tvChannel=(TextView) findViewById(R.id.tvChannel);
        tvDevice.setText("NOT ATTACHED");
        tvChannel.setText("CLOSE");

        Settings.context=getApplicationContext();
        Settings.Sdk.get().setDevice(Settings.Sdk.PREF_DEVICE_BLUETOOTH);
        MComm.getInstance().handlerCallback=handlerCallback;


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
        findViewById(R.id.btnSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBluetooth())
                    showBluetoothDialog();

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
        Log.d("BLUETOOTH",output);
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
                    Toast.makeText(BluetoothActivity.this, "ATTACH", Toast.LENGTH_LONG).show();
                } else if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
                    Toast.makeText(BluetoothActivity.this, "ATTACH", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(BluetoothActivity.this, "ATTACH", Toast.LENGTH_LONG).show();
                    ;//attach();
                    ;//startState();
                }
            } else if (action.equals(ConnectorIntent.ACTION_CLOSE)) {
                tvDevice.setText("DETACH");
                Log.d("ACTION", "ACTION CLOSE");
                Overlay.hide();
                Toast.makeText(BluetoothActivity.this, "DETACH", Toast.LENGTH_LONG).show();

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


    private final static String mpaySzReqUuid = "00001101-0000-1000-8000-00805F9B34FB";

    public class AsyncOpen extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                MComm myComm = MComm.getInstance();
                myComm.setUuid(mpaySzReqUuid);
                byte []string="12345678".getBytes();
                myComm.setMobileDeviceID(string);
                myComm.setBTdevice(Settings.JuspBT.get().getBluetoothDevice());
                int res= MComm.getInstance().Open();
                if(res!=0)
                    return false;
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
            if(res==true) {
                tvChannel.setText("OPEN");
                Toast.makeText(BluetoothActivity.this,"OPEN",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(BluetoothActivity.this,"FAIL OPEN",Toast.LENGTH_LONG).show();
            }

        }
    }


    public class AsyncState extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                MComm myComm = MComm.getInstance();
                myComm.setUuid(mpaySzReqUuid);
                byte []string="12345678".getBytes();
                myComm.setMobileDeviceID(string);
                myComm.setBTdevice(Settings.JuspBT.get().getBluetoothDevice());
                int res= MComm.getInstance().Open();
                if(res!=0)
                    return false;
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
            if(res==true) {
                tvChannel.setText("OPEN");
                Toast.makeText(BluetoothActivity.this,"OPEN",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(BluetoothActivity.this,"FAIL OPEN",Toast.LENGTH_LONG).show();
            }

        }
    }

    public class AsyncClose extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                MComm.getInstance().Close();
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
            if(res==true) {
                tvChannel.setText("CLOSE");
                Toast.makeText(BluetoothActivity.this,"CLOSE",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(BluetoothActivity.this,"FAIL CLOSE",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class AsyncSend extends AsyncTask<Void, Void, Boolean> {
        public byte []buffer;
        protected Boolean doInBackground(Void... voids) {
            try {
                MComm.getInstance().Write(buffer);
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
            Toast.makeText(BluetoothActivity.this,"SEND",Toast.LENGTH_LONG).show();
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


    ListBluetoothAdapter laBluetooth;
    List<Element> mBluetoothList= new ArrayList<Element>();


    public boolean checkBluetooth(){
        BluetoothAdapter mBluetoothAdapter = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth, we can do nothing
            AlertDialog myAlertDialog = new AlertDialog.Builder(this).create();
            myAlertDialog.setTitle("error");
            myAlertDialog.setMessage("error_bluetooth_unsupported");
            myAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            myAlertDialog.show();
            return false;
        }
        // request BT activation
        if (!mBluetoothAdapter.isEnabled()) {
            AlertDialog myAlertDialog = new AlertDialog.Builder(this).create();
            myAlertDialog.setTitle("error");
            myAlertDialog.setMessage("error_bluetooth_off");
            myAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
            });
            myAlertDialog.show();
            return false;
        }

        // Discover Paired Devices
        mBluetoothList.clear();
        Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        UUID mpayReqUuid = UUID.fromString(Settings.JuspBT.get().getPaxUuid());
        // If there are paired devices
        if (pairedDevicesSet.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevicesSet) {
                //    ParcelUuid[] devUids = device.getUuids();
                //    for (ParcelUuid ser : devUids)
                //    {
                //        UUID tmp = ser.getUuid();
                //        if (tmp.equals(mpayReqUuid)) {
                Element e = new Element(device.getName());

                if (e.name == Settings.JuspBT.get().getBluetoothDeviceName())
                    e.selected = true;
                mBluetoothList.add(e);
            }
        }
        // no found paired devices
        if ( mBluetoothList.size()==0){
            AlertDialog myAlertDialog = new AlertDialog.Builder(this).create();
            myAlertDialog.setTitle("error");
            myAlertDialog.setMessage("error_bluetooth_noassociation");
            myAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            myAlertDialog.show();
            return false;
        }else if (mBluetoothList.size()==1){
            mBluetoothList.get(0).selected=true;
        }
        return true;

    }

    public void showBluetoothDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_listview, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("settings");

        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        laBluetooth=new ListBluetoothAdapter(this,mBluetoothList);
        lv.setAdapter(laBluetooth);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        //lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (Element e:mBluetoothList)
                    e.selected=false;
                mBluetoothList.get(i).selected=true;
                laBluetooth.notifyDataSetChanged();
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // retrieve the selected bluetooth device
                for ( Element e: mBluetoothList){
                    if (e.selected){
                        // Save the bluetooth device name
                        Settings.JuspBT.get().setBluetoothDeviceName(e.name);
                        // test the device: try the connection
                        ConnectorIntent.get().attach();
                    }
                }
            }
        });
        alertDialog.show();
    }




    private class Element {
        private String name = "";
        private int resource = R.mipmap.ic_launcher;
        private boolean selected=false;
        Element(String name_){
            name=name_;
        }
    }
    private class ListBluetoothAdapter extends ArrayAdapter<Element> {
        private List<Element> elements=null;
        public ListBluetoothAdapter(Context context, List<Element> elements) {
            super(context, R.layout.listview_bluetooth, elements);
            this.elements=elements;
        }
        public View getView(final int position, View view, ViewGroup parent){
            LayoutInflater li= LayoutInflater.from(getContext());
            View v=li.inflate(R.layout.listview_bluetooth, null);
            TextView tvName=(TextView) v.findViewById(R.id.tvName);
            ImageView ivImage=(ImageView) v.findViewById(R.id.ivImage);
            if (elements.get(position).selected)
                tvName.setTextColor(getResources().getColor(R.color.colorAccent));
            else
                tvName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tvName.setText(elements.get(position).name);
            ivImage.setImageResource(elements.get(position).resource);
            return v;
        }
    }
}
