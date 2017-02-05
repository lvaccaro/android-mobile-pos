package vaccarostudio.com.gui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioTrack;


import java.util.Set;


/**
 * Created by lucavaccaro on 05/03/14.
 */
public class Settings {

    /* COMMON PREFERENCES */
    public static final String PREF = "JUSP";
    public static final String AMOUNT = "savedamount";
    public static Context context = null;


    private static Settings handle;
    public static Settings get(Context c){
        context=c;
        if (handle==null)
            handle=new Settings();
        return handle;
    }
    public static Settings get(){
        return handle;
    }


    /* PAX PARAMETERS */

    public static class JuspBT {
        static JuspBT handle;
        public static JuspBT get(){
            if (handle==null)
                handle=new JuspBT();
            return handle;
        }

        public static final String PREF_MOBILEDEVICE = "MOBILEDEVICE";
        public static final String PREF_DONGLEID = "DONGLEID";
        public static final String PREF_BLUETOOTHDEVICENAME= "BLUETOOTHDEVICENAME";
        private static final String mpaySzReqUuid = "00001101-0000-1000-8000-00805F9B34FB";
        private String mobileDeviceID = "";
        private String dongleID = null;
        private String bluetoothDeviceName = null;

        public String getMobileDeviceID() {
            if (context == null)
                return mobileDeviceID;
            return context.getSharedPreferences(Settings.PREF, Context.MODE_PRIVATE).getString(PREF_MOBILEDEVICE, mobileDeviceID);

        }

        public String getPaxUuid() {
            return mpaySzReqUuid;
        }

        public String getDongleID() {
            if (context == null)
                return dongleID;
            return context.getSharedPreferences(Settings.PREF, Context.MODE_PRIVATE).getString(PREF_DONGLEID, dongleID);
        }

        public String getBluetoothDeviceName() {
            if (context==null)
                return null;
            return context.getSharedPreferences(Settings.PREF, Context.MODE_PRIVATE).getString(PREF_BLUETOOTHDEVICENAME, null);
        }

        public void setDongleID(String dongle_ID_) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(PREF_DONGLEID, dongle_ID_).commit();
        }

        public void setMobileDeviceID(String mobileDevice_ID_) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(PREF_MOBILEDEVICE, mobileDevice_ID_).commit();
        }

        public void setBluetoothDeviceName(String devicename_) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(PREF_BLUETOOTHDEVICENAME, devicename_).commit();
        }

        public BluetoothDevice getBluetoothDevice() {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                return null;
            }
            // request BT activation
            if (!mBluetoothAdapter.isEnabled()) {
                return null;
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            BluetoothDevice mBluetoothDevice = null;
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(getBluetoothDeviceName())) {
                        mBluetoothDevice=device;
                    }
                }
            }
            if (mBluetoothDevice == null) {
                return null;
            }
            return mBluetoothDevice;
        }
    }

    /* PRINTER PARAMETERS */

    /* PRINTER PARAMETERS */
    public static class Sdk {
        private static Sdk handle;
        private String device = null;
        private String terminalID = null;
        private boolean attached=false;
        private static String token = null;
        private static String timestamp = null;
        //private String wsAddress="https://webservice.vaccarostudio.com";
        private String wsAddress="https://webservice.vaccarostudio.com";

        public static final String PREF_DEVICE = "DEVICE";
        public static final String PREF_DEVICE_AUDIO = "AUDIO";
        public static final String PREF_DEVICE_BLUETOOTH = "BLUETOOTH";
        public static final String PREF_DEVICE_NOTHING= "NOTHING";
        public static final String PREF_DEVICE_USB = "USB";

        public static Sdk get() {
            if (handle == null)
                handle = new Sdk();
            return handle;
        }
        public String getDevice() {
            if (context==null)
                return PREF_DEVICE_NOTHING;
            return context.getSharedPreferences(Settings.PREF, Context.MODE_PRIVATE).getString(PREF_DEVICE, PREF_DEVICE_NOTHING);
        }
        public String getTerminalID() {
            return terminalID;
        }
        public void setTerminalID(String terminal_id_) {
            terminalID=terminal_id_;
        }
        public  void setDevice(String device_) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(PREF_DEVICE, device_).commit();
        }


    }




    static public class Terminal {
        static Terminal handle;
        public static Terminal get(){
            if (handle==null)
                handle=new Terminal();
            return handle;
        }
        /* NETWORK PARAMETERS */
        // production
        private String gtAddress = "5.10.93.150";
        private String gtPort = "10521";
        private String gtCode="088105";
        private Boolean protocolHead=true;
        //testing
/*    private static String gtAddress = "81.95.157.106";
    private static String gtPortBt = "10501";
    private static String gtPortCogeban = "10521";
    private static String gtCode="088105";
    private static String wsAddress="https://webservicetest.vaccarostudio.com";
    private static Boolean protocolCogeban=false;
*/
        public void setGtAddress(String gtAddress_){
            gtAddress=gtAddress_;
        }
        public String getGtAddress(){
            return gtAddress;
        }
        public void setGtPort(String gtPort_){
            gtPort=gtPort_;
        }
        public String getGtPort(){
            return gtPort;
        }
        public String getGtCode(){
            return gtCode;
        }
        public Boolean getProtocolHead() {
            return protocolHead;
        }
        public void setProtocolHead(Boolean protocol) {
            protocolHead=protocol;
        }
    }

    public static  class Printer {
        public static String getPrinterDeviceName() {
            return context.getSharedPreferences("printer", Context.MODE_PRIVATE).getString("devicename", null);
        }
        public static void setPrinterDeviceName(String devicename) {
            context.getSharedPreferences("printer", Context.MODE_PRIVATE).edit().putString("devicename", devicename).commit();
        }
        public static String getIp(){
            SharedPreferences settings = context.getSharedPreferences("printer", Context.MODE_PRIVATE);
            return settings.getString("ip", null);
        }
        public static String getPort(){
            SharedPreferences settings = context.getSharedPreferences("printer", Context.MODE_PRIVATE);
            return settings.getString("port", null);
        }

        public static void setIpPort(String ip, String port) {
            context.getSharedPreferences("printer", Context.MODE_PRIVATE).edit().putString("ip", ip).commit();
            context.getSharedPreferences("printer", Context.MODE_PRIVATE).edit().putString("port", port).commit();
        }
    }

    public static  class Jusp {
        static Jusp handle;
        public static Jusp get(){
            if (handle==null)
                handle=new Jusp();
            return handle;
        }
        /* AUDIO PARAMETERS */
        private  float volumeIN = 1;
        private  float volumeOUT = 1;
        private  int samplerate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);
        private int baudrate=2000;
        private boolean doCalibration=false;

        public  void setVolumeIN(float volumeIN) {
            this.volumeIN = volumeIN;
        }

        public  void setVolumeOUT(float volumeOUT) {
            this.volumeOUT = volumeOUT;
        }

        public  void setSamplerate(int samplerate) {
            this.samplerate = samplerate;
        }

        public  float getVolumeIN() {
            return volumeIN;
        }
        public  float getVolumeOUT() {
            return volumeOUT;
        }
        public  int getSamplerate() {
            return samplerate;
        }

        public void setDoCalibration(boolean doCalibration){
            this.doCalibration=doCalibration;

        }
        public  boolean getDoCalibration(){
            return this.doCalibration;
        }

        public  void setBaudrate(int baudrate) {
            this.baudrate = baudrate;
        }

        public int getBaudrate() {
            return baudrate;
        }
    }

    public static final int SUCCESS=000;
    public static final int FAILURE=001;
    public static final int ERROR_CREDENTIAL=100;
    public static final int ERROR_NETWORK=101;
    public static final int ERROR_MAGNETIC_INVALID=102;
    public static final int ERROR_DEVELOPER_TOKEN_INVALID=103;
    public static final int ERROR_DEVICE_NONE=104;
    public static final int ERROR_COMMUNICATION=105;
    public static final int ERROR_NOCONFIGURED=106;
    public static final int ERROR_INVALID_SESSION=107;
    public static final int ERROR_INVALID_TERMINAL=108;
    public static final int ERROR_APP2APP_NONE=109;
    public static final int ERROR_CANCEL=110;

    public static final int SIGNATURE=201;
    public static final int RECEIPT=202;

    public static final int ERROR_INVALID_OPERATION=111;

}
