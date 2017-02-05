package vaccarostudio.com.verifone;

import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import vaccarostudio.com.gui.Settings;

/**
 * Created by lucavaccaro on 25/02/14.
 */
public class ConnectorIntent extends IntentService {
    private String TAG = "Connector";
    public static final String ACTION_USBATTACH = "vaccarostudio.com.USB_ATTACH";
    public static final String ACTION_USBDETACH = "vaccarostudio.com.USB_DETACH";
    public static final String ACTION_USBPERMISSION = "vaccarostudio.com.USB_USBPERMISSION";
    public static final String ACTION_BLUETOOTHATTACH = "vaccarostudio.com.BLUETOOTH_ATTACH";
    public static final String ACTION_BLUETOOTHDETACH = "vaccarostudio.com.BLUETOOTH_DETACH";
    public static final String ACTION_DISCOVER = "vaccarostudio.com.DISCOVER";
    public static final String ACTION_UUID = "vaccarostudio.com.UUID";
    public static final String ACTION_CLOSE = "vaccarostudio.com.CLOSE";
    public static final String ACTION_OPEN = "vaccarostudio.com.OPEN";
    public static final String ACTION_SYNCHRONIZE = "vaccarostudio.com.SYNCHRONIZE";

    private static BluetoothAdapter mBluetoothAdapter = null;
    private static Context context=null;

    public ConnectorIntent() {
        super("ConnectIntent");
    }


    private static ConnectorIntent  handler;


    public static ConnectorIntent get() {
        if (handler == null)
            handler = new ConnectorIntent();
        return handler;
    }


    public void onCreate(Context context_) {
        context=context_;
        if (context==null)
            return;
        Settings.context = context;
        notify_synchronization();

    }

    public void onResume(Context context_){
        context=context_;
        if (context==null)
            return;
        Settings.context = context;
        // Register event on ConnectorRegister
        IntentFilter intentFilter=new IntentFilter();
        if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_AUDIO)) {
            ignore_first_audio_signal=true;
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        }else if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)){
            //intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            intentFilter.addAction(ConnectorRegister.ACTION_USB_PERMISSION);
        }else if (Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_BLUETOOTH) ) {
        }
        ConnectorRegister connectorRegister = ConnectorRegister.getInstance();
        context.registerReceiver(connectorRegister, intentFilter);

    }

    public void onPause(Context context_){
        context=context_;
        if (context==null)
            return;
        // Register event on ConnectorRegister
        ConnectorRegister connectorRegister = ConnectorRegister.getInstance();
        context.unregisterReceiver(connectorRegister);
    }

    static boolean usbconnect=false;

    /* USB Monitor to check the usb */
    static Handler hMonitor = new Handler();
    Runnable rMonitor = new Runnable(){
        public void run() {
            if(context==null)
                return;
            HashMap.Entry<String, UsbDevice> device=null;
            boolean found=false;
            UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
                if (entry.getValue().getVendorId() == 1003 && entry.getValue().getProductId() == 24580) {
                    device=entry;
                    found=true;
                }
            }

            if (usbconnect==false && found==true && Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)){
               // FILTER USB PERMISSION
                usbconnect=true;
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ConnectorRegister.ACTION_USB_PERMISSION), 0);
                manager.requestPermission(device.getValue(), mPermissionIntent);
            }
            else if (usbconnect==true && found==false && Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)) {
                usbconnect=false;
                notify_detach();
            }
            hMonitor.postDelayed(rMonitor, 1000);
        }
    };

    private static boolean isAttached=false;

    public static boolean isAttached(){
        return isAttached;
    }

    public void attach(){
        String dev=Settings.Sdk.get().getDevice();
        Intent service ;
        if(context==null)
            service= new Intent(getBaseContext(), ConnectorIntent.class);
        else
            service= new Intent(context, ConnectorIntent.class);
        if (dev.equals(Settings.Sdk.PREF_DEVICE_USB)) {
            service.setAction(ConnectorIntent.ACTION_USBPERMISSION);
        } else if (dev.equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
            service.setAction(ConnectorIntent.ACTION_BLUETOOTHATTACH);
        }
            if(context==null)
                startService(service);
            else
                context.startService(service);
    }
    public void detach(){
        String dev=Settings.Sdk.get().getDevice();
        Intent service ;
        if(context==null)
            service= new Intent(getBaseContext(), ConnectorIntent.class);
        else
            service= new Intent(context, ConnectorIntent.class);
        if (dev.equals(Settings.Sdk.PREF_DEVICE_USB)) {
            service.setAction(ConnectorIntent.ACTION_USBDETACH);
        } else if (dev.equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)){
            service.setAction(ConnectorIntent.ACTION_BLUETOOTHDETACH);
        }
        if(context==null)
            startService(service);
        else
            context.startService(service);

    }

    private void notify_attach() {
        Intent resultBroadCastIntent = new Intent();
        resultBroadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        resultBroadCastIntent.setAction(ACTION_OPEN);
        isAttached=true;
        if(context==null)
            sendBroadcast(resultBroadCastIntent);
        else
            context.sendBroadcast(resultBroadCastIntent);
    }
    private void notify_detach() {
        ;//Close close=new Close();
        ;//close.start();
        Intent resultBroadCastIntent = new Intent();
        resultBroadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        resultBroadCastIntent.setAction(ACTION_CLOSE);
        isAttached=false;
        if(context==null)
            sendBroadcast(resultBroadCastIntent);
        else
            context.sendBroadcast(resultBroadCastIntent);
    }
    private void notify_synchronization() {
        Intent resultBroadCastIntent = new Intent();
        resultBroadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        resultBroadCastIntent.setAction(ACTION_SYNCHRONIZE);
        if(context==null)
            sendBroadcast(resultBroadCastIntent);
        else
            context.sendBroadcast(resultBroadCastIntent);
    }



    private void requestPermission(){

        String dev=Settings.Sdk.get().getDevice();
        if (dev!=null && dev.equals(Settings.Sdk.PREF_DEVICE_USB)) {
            UsbManager manager ;
            if (context!=null)
                manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            else
                manager = (UsbManager) getApplication().getSystemService(Context.USB_SERVICE);

            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
                    if ((entry.getValue().getVendorId() == 1003 && entry.getValue().getProductId() == 24580) ||
                            (entry.getValue().getVendorId() == 0x11ca && entry.getValue().getProductId() == 0x0241))
                    {
                        PendingIntent mPermissionIntent;
                        if (context!=null)
                            mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ConnectorRegister.ACTION_USB_PERMISSION), 0);
                        else
                            mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ConnectorRegister.ACTION_USB_PERMISSION), 0);
                        manager.requestPermission(entry.getValue(), mPermissionIntent);
                    }
            }
        }
    }
    private boolean checkPermission(){
        String dev=Settings.Sdk.get().getDevice();
        if (dev!=null && dev.equals(Settings.Sdk.PREF_DEVICE_USB)) {

            String permission = ConnectorRegister.ACTION_USB_PERMISSION;
            if (context==null)
                return false;
            int res = context.checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    public boolean discover(){
        String dev=Settings.Sdk.get().getDevice();
        if (dev.equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.cancelDiscovery();
            Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
            UUID mpayReqUuid = UUID.fromString(Settings.JuspBT.get().getPaxUuid());
            String mChosenDevice = null;
            // If there are paired devices
            if (pairedDevicesSet.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevicesSet) {
                    //ParcelUuid[] devUids = device.getUuids();
                    //for (ParcelUuid ser : devUids) {
                    //    UUID tmp = ser.getUuid();
                    //    if (tmp.equals(mpayReqUuid)) {
                    mChosenDevice = device.getName() + "\n" + device.getAddress();
                    //Log.d(TAG,"uuid: "+device.getUuids()[0].getUuid().toString()); <-- error
                    //    }
                    //}
                }
            }
            if (mChosenDevice == null)
                return false;
            else
                return true;
        } else if (dev.equals(Settings.Sdk.PREF_DEVICE_USB)) {
            UsbManager manager;
                if (context==null)
                    manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                else
                    manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
                    if( entry.getValue().getVendorId()==1003 &&  entry.getValue().getProductId()==24580 ){
                        // FILTER USB PERMISSION
                        //if (checkPermission()==false)
                        //    requestPermission();
                        return true;
                    }
                }

        }
        return false;
    }


    public void synchronize() {
        Intent resultBroadCastIntent = new Intent();
        resultBroadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        resultBroadCastIntent.setAction(ACTION_SYNCHRONIZE);
        if(context==null)
            sendBroadcast(resultBroadCastIntent);
        else
            context.sendBroadcast(resultBroadCastIntent);
    }
    static boolean ignore_first_audio_signal=true;

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (intent==null || action==null)
            return;
        if (action.equals(ConnectorIntent.ACTION_USBATTACH)&& Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)) {
            if (context==null)
                return;
            usbconnect=true;
            notify_attach();
        } else if (action.equals(ConnectorIntent.ACTION_USBDETACH)&& Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)) {
            if (context==null)
                return;
            usbconnect=false;
            notify_detach();
        } else if (action.equals(ConnectorIntent.ACTION_USBPERMISSION)) {

            Log.d("ConnectorIntent","ACTION_USBPERMISSION");
            usbconnect=true;
            if (isAttached==false && Settings.Sdk.get().getDevice().equals(Settings.Sdk.get().PREF_DEVICE_USB)){
                if (checkPermission()) {
                    usbconnect=true;
                    notify_attach();
                }else {
                    requestPermission();
                }
            }
        } else if (action.equals(ConnectorIntent.ACTION_BLUETOOTHDETACH) && Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
            if (context==null)
                return;
            notify_detach();
        } else if (action.equals(ConnectorIntent.ACTION_BLUETOOTHATTACH) && Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
            if (context==null)
                return;
            notify_attach();
        } else if (action.equals(ConnectorIntent.ACTION_DISCOVER) && Settings.Sdk.get().getDevice().equals(Settings.Sdk.PREF_DEVICE_BLUETOOTH)) {
            if ( discover() == true)
                notify_attach();
        } else if (action.equals(ConnectorIntent.ACTION_UUID)) {
            synchronize();
        } else if (action.equals(ConnectorIntent.ACTION_SYNCHRONIZE)) {
            synchronize();
        }else {

            Log.d("ConnectorIntent","ELSE");
        }

    }

}

