package vaccarostudio.com.verifone;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.net.LocalSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.Semaphore;


/* this class is a singleton in order to share the same connection between all activities */
public class MComm {

    private static MComm _instance;
    private BluetoothDevice commDevice = null;
    private UUID commUuid = null;
    private BluetoothSocket commSocket = null;
    private boolean bluetooth=true;
    private boolean connected=false;


    private static byte frame_counter=0;
    public static final int SUCCESS = 0;
    public static final int ERR_INTERNAL = -1;
    public static final int ERR_NOT_INITIALIZED = -2;
    private static final String TAG = "XXX";


    public static Handler handlerCallback=null;

    private MComm() {
        ;
    }

    public byte [] defaultMobDevID = {'a', 'n', 'd', 'r', 'o', 'i', 'd','-', 't', 'e', 's', 't', '-', '0','0','0'};
    public byte [] defaultDongleID = {'0', '0','0','0','0','0','0','0' };
    public static byte []user_id_mobile_payment_system={'a', 'n', 'd', 'r', 'o', 'i', 'd','-', 't', 'e', 's', 't', '-', '0','0','0'};


    public void setMobileDeviceID(byte[] str){
        //this.defaultMobDevID=str.clone();
        //this.user_id_mobile_payment_system=str.clone();
    }
    public byte[] getMobileDeviceID(){
        return this.defaultMobDevID.clone();
    }
    public void setDongleID(byte[] str){
        this.defaultDongleID=str.clone();
    }
    public byte[] getDongleID(){
        return this.defaultDongleID.clone();
    }




    // Ready callback function
    public Function fResult=null;
    public Function fTicket=null;
    public Function fError=null;
    public Function fAuto=null;
    public Function fAcquirer=null;


    private Semaphore session = new Semaphore(1);
    private Packet reqPkt=null;
    private boolean isResultDone=true;

    public static MComm getInstance( ) {
        if (_instance == null) {
            synchronized ( MComm.class )	{
                if (_instance == null) {
                    _instance = new MComm();

                }
            }
        }
        return _instance;
    }

    public void setBTdevice ( BluetoothDevice dev ) {
        commDevice = dev;
    }

    public BluetoothDevice getBTdevice ()
    {
        return commDevice;
    }

    public void setUuid ( String szUuid ) {

        commUuid = UUID.fromString(szUuid);
    }


    public boolean open1() {
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            commSocket = commDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);

            commSocket.connect();
            Log.e(TAG, "Connected");
            connected = true;

            mmInStream = commSocket.getInputStream();
            mmOutStream = commSocket.getOutputStream();

            // Start the thread to manage the connection and perform transmissions
            Log.d("mConnectedThread", "");
            if (mConnectedThread != null) {
                mConnectedThread.close();
                mConnectedThread = null;
            }
            mConnectedThread = new ConnectedThread(commSocket);
            mConnectedThread.start();
            session = new Semaphore(1);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "createInsecureRfcommSocketToServiceRecord : "+e.getMessage());
            return false;
        }
    }
    public boolean open2() {
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            commSocket = commDevice.createRfcommSocketToServiceRecord(MY_UUID);
            commSocket.connect();
            Log.e(TAG, "Connected");
            connected = true;

            mmInStream = commSocket.getInputStream();
            mmOutStream = commSocket.getOutputStream();

            // Start the thread to manage the connection and perform transmissions
            Log.d("mConnectedThread", "");
            if (mConnectedThread != null) {
                mConnectedThread.close();
                mConnectedThread = null;
            }
            mConnectedThread = new ConnectedThread(commSocket);
            mConnectedThread.start();
            session = new Semaphore(1);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "createRfcommSocketToServiceRecord : "+e.getMessage());
            return false;
        }
    }
    public boolean open3() {
        try {
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            // since android 4.2 bluetooth stack has changed.
            // exception: "read failed, socket might closed or timeout, read ret: -1"
            commSocket = (BluetoothSocket) commSocket.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(commSocket, 1);
            commSocket.connect();

            Log.e(TAG, "Connected");
            connected = true;

            // Start the thread to manage the connection and perform transmissions
            if (mConnectedThread != null) {
                mConnectedThread.close();
                mConnectedThread = null;
            }
            mConnectedThread = new ConnectedThread(commSocket);
            mConnectedThread.start();
            session = new Semaphore(1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "commSocket.getClass().getMethod : " + e.getMessage());
            return false;
        }
    }
    public boolean open4() {
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            commSocket = commDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            commSocket.connect();
            Log.e(TAG, "Connected");
            connected = true;

            mmInStream = commSocket.getInputStream();
            mmOutStream = commSocket.getOutputStream();

            // Start the thread to manage the connection and perform transmissions
            Log.d("mConnectedThread", "");
            if (mConnectedThread != null) {
                mConnectedThread.close();
                mConnectedThread = null;
            }
            mConnectedThread = new ConnectedThread(commSocket);
            mConnectedThread.start();
            session = new Semaphore(1);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "createInsecureRfcommSocketToServiceRecord 5sec : "+e.getMessage());
            return false;
        }
    }
    public boolean open5() {
        try {
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            // since android 4.2 bluetooth stack has changed.
            // exception: "read failed, socket might closed or timeout, read ret: -1"
            commSocket = (BluetoothSocket) commSocket.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(commSocket, MY_UUID);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            commSocket.connect();

            Log.e(TAG, "Connected");
            connected = true;

            // Start the thread to manage the connection and perform transmissions
            if (mConnectedThread != null) {
                mConnectedThread.close();
                mConnectedThread = null;
            }
            mConnectedThread = new ConnectedThread(commSocket);
            mConnectedThread.start();
            session = new Semaphore(1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "commSocket.getClass().getMethod 5 sec: " + e.getMessage());
            return false;
        }
    }

    public int Open ( ) {


        if (bluetooth && ((commUuid == null) || (commDevice == null))) {
            if (fError != null) {
                fError.setData(null);
                fError.run();
            }
            return ERR_NOT_INITIALIZED;
        }



        if (bluetooth) {
            // start a connection with selDev, UUID is 00001101-0000-1000-8000-00805F9B34FB
            // Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            // Get a BluetoothSocket to connect with the given BluetoothDevice

            Close();

            try {
                UUID MY_UUID2 = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                Log.d(TAG, "UUID.fromString :" + MY_UUID2.toString());
                UUID MY_UUID1 = commDevice.getUuids()[0].getUuid();
                Log.d(TAG, "UUID commDevice.getUuids()[0].getUuid() :" + MY_UUID1.toString());
            }catch (Exception e){
                Log.d(TAG,"UUID errors");
            }


                if (open1()==false) {
                    if (open2() == false)
                        if (open3() == false)
                            if (open4() == false)
                                if (open5() == false) {
                                    Log.e(TAG, "Couldn't establish Bluetooth connection!");
                                    Close();
                                    if (fError != null) {
                                        fError.setData(null);
                                        fError.run();
                                    }
                                    return ERR_INTERNAL;
                                }
                }
        }else {
            // USB MODE
            try {
                connected=true;
                SerialIO.Open();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("USB",e.getMessage());
                Close();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
            }
        }


        if (fResult!=null){
            fResult.setData(null);
            fResult.run();
        }
        return SUCCESS;
    }

    public void Close ( ) {
        Log.d(TAG,"Close");
        connected=false;

        if (bluetooth) {
            // BLUETOOTH MODE
            if (commSocket != null) {
                    //commSocket.close();
                    session.release();
                    if (mConnectedThread != null) {
                        mConnectedThread.close();
                        mConnectedThread = null;
                    }
                cleanClose(commSocket);
                commSocket = null;
                    if (session!=null)
                        session.release();
                    if (fError != null) {
                        fError.setData(null);
                        fError.run();
                    }
            }
        } else {
            // USB MODE
            try {
                SerialIO.Close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Close: exception" + e.getMessage());
                session.release();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
            }
        }
        if (fResult!=null){
            fResult.setData(null);
            fResult.run();
        }
    }


    public synchronized  int Send ( Packet msg ) {
        msg.setCounter(frame_counter);
        if(msg.getMessageType()== Packet.Type.__MPOS_ACK || msg.getMessageType()== Packet.Type.__MPOS_NAK){
            // useless information packet
            ;
        }else if ( msg.getMessageType()==Packet.Type.__MPOS_AUTHENTICATION_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_STATE_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_AUTO_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_FIRST_DLL_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_CLOSURE_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_TOTALS_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_LOG_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_PAYMENT_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_REVERSAL_REQ ||
                msg.getMessageType()==Packet.Type.__MPOS_MENU_REQ
                ) {
            // start of the operation
            reqPkt=(Packet)msg;
            isResultDone=false;

            //clean previous operation
            if (fResult!=null)
                fResult.setData(null);
            if (fTicket!=null)
                fTicket.setData(null);
        }
        //send packet
        int ret=sendData(msg.toBytes());
        logcat("YYY OUT",msg.toFormatString());
        return ret;
    }


    public boolean Write (byte[]toSend){
        if ( bluetooth ) {
            //BLUETOO TH MODE
            try {
                mConnectedThread.write(toSend);
                toSend = null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Close();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
                return false;
            }
        }else {
            //USB MODE
            try {
                SerialIO.Send(toSend);
                toSend=null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Close();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
                return false;
            }
        }
    }

    private int sendData ( byte[] data) {

        byte[] toSend = new byte[data.length + 6];
        byte[] ackBuffer = null;
        int iRet;
        long compCRC;

        toSend[0] = 0x02;
        for (int i = 0; i < data.length; i++)
        {
            toSend[i+1] = data[i];
        }
        toSend[data.length+1] = 0x03;

        compCRC = CRC32tab.crc32(data);

        toSend[data.length+2] = (byte) ((compCRC >>> 24) & 0xff);
        toSend[data.length+3] = (byte) ((compCRC >>> 16) & 0xff);
        toSend[data.length+4] = (byte) ((compCRC >>> 8) & 0xff);
        toSend[data.length+5] = (byte) ( compCRC & 0xff);

        if ( bluetooth ) {
            //BLUETOO TH MODE
            try {
                mConnectedThread.write(toSend);
                toSend = null;
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Close();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
                return ERR_INTERNAL;
            }
        }else {
            //USB MODE
            try {
                SerialIO.Send(toSend);
                toSend=null;
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Close();
                if (fError != null) {
                    fError.setData(null);
                    fError.run();
                }
                return ERR_INTERNAL;
            }
        }
    }


    enum States{
        SOH,
        HEADER,
        BODY,
        ETX,
        CRC
    };
    States state= States.SOH;
    int idxHeader=0,idxBody=0,iBodyLen=0,idxCrc=0;
    public boolean automa (byte []raw,byte b){
        switch(state){
            case SOH:
                if (b==0x02){
                    idxHeader=0;
                    state= States.HEADER;
                    //;//Log.d(TAG,"BUFFER SOH "+String.format("%02x",b));
                }
                break;
            case HEADER:
                if (idxHeader==32-1){
                    idxBody=0;
                    iBodyLen = 0;
                    iBodyLen+=((((int)(raw[idxHeader+1-3])) & 0xff) * 0x1000000);
                    iBodyLen+=((((int)(raw[idxHeader+1-2])) & 0xff) * 0x10000);
                    iBodyLen+=((((int)(raw[idxHeader+1-1])) & 0xff) * 0x100);
                    iBodyLen+=(((int) raw[idxHeader+1]) & 0xff);
                    if(iBodyLen==0)
                        state= States.ETX;
                    else
                        state= States.BODY;

                }else if (idxHeader<32){
                    idxHeader++;
                }else
                    state= States.SOH;

                //;//Log.d(TAG,"BUFFER HEADER "+String.format("%02x",b));
                break;
            case BODY:
                if (idxBody==iBodyLen-1){
                    state= States.ETX;
                }else if(idxBody<iBodyLen){
                    idxBody++;
                }else
                    state= States.SOH;
                //;//Log.d(TAG,"BUFFER BODY "+String.format("%02x",b));
                break;
            case ETX:
                if (b==0x03){
                    state= States.CRC;
                    idxCrc=0;
                }else
                    state= States.SOH;

                //;//Log.d(TAG,"BUFFER ETX "+String.format("%02x",b));
                break;
            case CRC:
                if(idxCrc==4-1){

                    //;//Log.d(TAG,"BUFFER FINISH "+String.format("%02x",b));
                    state= States.SOH;
                    return true;
                }else if(idxCrc<4){
                    idxCrc++;
                }else
                    state= States.SOH;

                //;//Log.d(TAG,"BUFFER CRC "+String.format("%02x",b));
                break;
            default:
                return false;
        }

        return false;
    }


    public boolean isConnected(){
        if (bluetooth) {
            if (commSocket == null)
                return false;
            else {
                // Apparently there are devices and/or Android versions where isConnected() is implemented (i.e. the function exists and you can call it), but worse than useless because it always returns false. I have an Alcatel phone in on my desk running Android 4.2.2 which exhibits this behavior.
                // https://stackoverflow.com/questions/14792040/android-bluetoothsocket-isconnected-always-returns-false/28372763#28372763
                //return commSocket.isConnected();
                return connected;
            }
        } else {
            return connected;
        }
    }

    static byte[] pkt = new byte[16000];
    static int pktIdx=0;

    public static void Read(byte data){
            pkt[pktIdx]=data;
            //Log.d(TAG,"Read : "+String.format("%02x",data));
            /*if(automa(pkt,pkt[pktIdx])==true){
                String str1="";
                for (int j=0;j<pktIdx;j++)
                    str1+=String.format("%02x ", pkt[j]);
                Log.d(TAG+" IN",str1);
                byte []pkt_=new byte[pktIdx-5];
                System.arraycopy(pkt, 1, pkt_, 0, pktIdx-5);
                final Packet msg= new Packet(pkt_);
                frame_counter=(byte) (msg.getCounter()+1);
                parser(msg);
                pktIdx= 0;
            }else
                pktIdx++;*/

    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private ConnectedThread mConnectedThread;
    BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    private class ConnectedThread extends Thread {
        private volatile boolean running = false;
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                running = true;
            } catch (Exception e) {
                Log.e(TAG, "ConnectedThread temp sockets not created", e);
                Close();
                if (fError!=null){
                    fError.setData(null);
                    fError.run();
                }
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void close(){
            try {
                /*if (mmInStream!=null)
                    mmInStream.close();
                if (mmOutStream!=null)
                    mmOutStream.close();
                if (mmSocket!=null)
                    mmSocket.close();
*/
                    //this.interrupt();
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "ConnectedThread close");
            }

        }

        public void run() {
            Looper.prepare();
            Log.i(TAG, "ConnectedThread BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            byte[] pkt = new byte[16000];
            int bytes=0,pktIdx=0;
            // Keep listening to the InputStream while connected
            while (running) {
                try {
                    // Read from the InputStream
                    if (MComm.this.isConnected() && mmSocket!=null && mmInStream.available()>0){
                        bytes = mmInStream.read(buffer);

                        for (int i=0;i<bytes;i++){
                            pkt[pktIdx]=buffer[i];
                            if(automa(pkt,pkt[pktIdx])==true){
                                String str1="";
                                for (int j=0;j<pktIdx;j++)
                                    str1+= String.format("%02x ", pkt[j]);
                                Log.d(TAG+" IN",str1);
                                byte []pkt_=new byte[pktIdx-5];
                                System.arraycopy(pkt, 1, pkt_, 0, pktIdx-5);
                                final Packet msg= new Packet(pkt_);
                                frame_counter=(byte) (msg.getCounter()+1);
                                parser(msg);
                                pktIdx= 0;
                            }else
                                pktIdx++;
                        }

                        String output="IN:";
                        for (int i=0;i<bytes;i++)
                            output+=String.format("%02x",buffer[i]);
                        output+="\n";
                        Log.d("SERIAL",output);

                        if (handlerCallback!=null){
                            Message msg = handlerCallback.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("string", output);
                            msg.setData(bundle);
                            handlerCallback.sendMessage(msg);
                        }
                    }

                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);

                } catch (Exception e) {
                    Log.e(TAG, "ConnectedThread disconnected");
                    Close();
                    if (fError!=null){
                        fError.setData(null);
                        fError.run();
                    }
                    return;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public synchronized void write(byte[] buffer) throws IOException {
            try{
                mmOutStream.write(buffer);
                String str1="";
                for (int i=0;i<buffer.length;i++)
                    str1+= String.format("%02x ", buffer[i]);
                Log.d(TAG + " OUT", str1);
                frame_counter++;
            } catch (Exception e) {
                Log.e(TAG, "disconnected");
                Close();
                if (fError!=null){
                    fError.setData(null);
                    fError.run();
                }
                return;
            }
        }

        public void cancel() {
            this.interrupt();
            //try {
                //mmSocket.close();
            //} catch (IOException e) {
                //Log.e(TAG, "close() of connect socket failed", e);
                if (fError!=null){
                    fError.setData(null);
                    fError.run();
                }
            //}
        }
    }

    private synchronized  void logcat(String title, String veryLongString){
        int start=0;
        for(int i = 0; i < veryLongString.length(); i++) {
            if (veryLongString.charAt(i)=='\n') {
                Log.d(title, veryLongString.substring(start, i));
                start = i + 1;
            }
        }
    }

    protected synchronized void parser(final Packet msg) {

        logcat("YYY IN",msg.toFormatString());

        //check if packet is Ack or Nak or other
        switch(msg.getMessageType()){
            case __MPOS_ACK:
                return;
            case __MPOS_NAK:
                if (fError!=null){
                    fError.setData(null);
                    fError.run();
                }
                return;
            default:
                Packet ack=new Packet(Packet.Type.__MPOS_ACK);
                Send(ack);
        };

        try {
            switch(msg.getMessageType()){
                case __MPOS_AUTHENTICATION_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            Packet pkt=msg;
                            if (reqPkt.getMessageType()==Packet.Type.__MPOS_AUTHENTICATION_REQ && fResult!=null)
                                fResult.setData(pkt);
                            else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_AUTHENTICATION_REQ && fTicket!=null){
                                fTicket.setData(pkt);
                            }
                        }
                    }.start();
                    break;
                case __MPOS_STATE_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            Packet pkt=msg;
                            if (reqPkt.getMessageType()==Packet.Type.__MPOS_STATE_REQ && fResult!=null)
                                fResult.setData(pkt);
                            else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_STATE_REQ && fTicket!=null){
                                fTicket.setData(pkt);
                            }
                        }
                    }.start();
                    break;
                case __MPOS_FIRST_DLL_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            Packet pkt=msg;
                            if (reqPkt.getMessageType()==Packet.Type.__MPOS_FIRST_DLL_REQ && fResult!=null)
                                fResult.setData(pkt);
                            else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_FIRST_DLL_REQ && fTicket!=null){
                                fTicket.setData(pkt);
                            }
                        }
                    }.start();
                    break;
                case __MPOS_PAYMENT_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_PAYMENT_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_PAYMENT_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_REVERSAL_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_REVERSAL_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_REVERSAL_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_TOTALS_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_TOTALS_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_TOTALS_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_CLOSURE_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_CLOSURE_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_CLOSURE_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_LOG_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_LOG_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_LOG_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_MENU_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_MENU_REQ && fResult!=null)
                                    fResult.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_MENU_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_AUTO_REQ:
                {
                    if (isResultDone==true){
                        // ok, perform the operation
                        isResultDone=true;
                        new Thread(){
                            @Override
                            public void run(){
                                try {
                                    reqPkt=msg;
                                    if (reqPkt.getMessageType()==Packet.Type.__MPOS_AUTO_REQ && fAuto!=null)
                                        fAuto.setData(reqPkt);
                                    else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_AUTO_REQ && fTicket!=null){
                                        fTicket.setData(reqPkt);
                                    }

                                    Packet msg=new Packet(Packet.Type.__MPOS_AUTO_RES);
                                    byte []state={(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
                                    msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),state));
                                    Send(msg);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        ;// wait, there is another operation

                    }

                }
                break;
                case __MPOS_AUTO_RES:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (reqPkt.getMessageType()==Packet.Type.__MPOS_AUTO_REQ && fAuto!=null)
                                    fAuto.setData(pkt);
                                else if (reqPkt.getMessageType()!=Packet.Type.__MPOS_AUTO_REQ && fTicket!=null){
                                    fTicket.setData(pkt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case __MPOS_ACQUIRER_REQ:
                    isResultDone=true;
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Packet pkt=msg;
                                if (pkt.getMessageType()==Packet.Type.__MPOS_ACQUIRER_REQ && fAcquirer!=null){
                                    fAcquirer.setData(pkt);
                                    fAcquirer.run();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    break;
                case __MPOS_END_SESSION_RES:
                    if(isResultDone==true) {
                        if(fResult!=null && fResult.getData()!=null)
                            fResult.run();
                        if(fTicket!=null && fTicket.getData()!=null)
                            fTicket.run();
                    }else{
                        Log.d(TAG,"Result done=false");
                        if (fError!=null){
                            fError.setData(null);
                            fError.run();
                            isResultDone=true;
                        }
                    }
                    break;
                case __MPOS_CONNECTION_REQ:
                    // connect to gt
                {
                    byte[]result=new byte[1];
                    Packet res=new Packet(Packet.Type.__MPOS_CONNECTION_RES);
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),MComm.user_id_mobile_payment_system));
                    if ( Remote.Connect() == true )
                        result[0]=0;
                    else
                        result[0]=12;
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),result));
                    Send(res);
                    res=null;
                }
                break;
                case __MPOS_SEND_DATA_REQ:
                    // send data from pax to gt
                {
                    byte[]result=new byte[1];
                    final Tlv tlv= msg.searchTlv(Tlv.Tags.__TAG_ISO8583.get());

                    if ( Remote.Send( tlv.getData() ) == true )
                        result[0]=0;
                    else
                        result[0]=12;

                    Packet res=new Packet(Packet.Type.__MPOS_SEND_DATA_RES);
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),msg.searchTlv(Tlv.Tags.__TAG_TERMINALID.get()).getData()));
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),result));
                    Send(res);
                    res=null;
                }
                break;
                case __MPOS_INFOCHIP_RES:
                    // send data from pax to gt
                {
                    int a =1;

                }
                break;
                case __MPOS_DISCONNECT_REQ:
                    // close gt socket
                {
                    byte[]result=new byte[1];
                    Packet res=new Packet(Packet.Type.__MPOS_DISCONNECT_RES);
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                    //res.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),msg.searchTlv(Tlv.Tags.__TAG_TERMINALID.get()).getData()));

                    if ( Remote.Disconnect() == true )
                        result[0]=0;
                    else
                        result[0]=12;
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),result));
                    Send(res);
                    res=null;
                }
                break;
                case __MPOS_GET_DATA_REQ:
                    // send data from gt to pax
                {
                    int recv=0;
                    for (Tlv tlv:msg.tlvs){
                        if (tlv.getType()== Tlv.Tags.__TAG_READ_MAX_BYTES.get()){
                            int lenlen=tlv.getDataLength();
                            byte []data=tlv.getData();
                            if (lenlen== 1){
                                recv+=((((int) data[0])) & 0xff);
                            }else if (lenlen == 2){
                                recv+=((((int) data[0]) & 0xff) * 0x100);
                                recv+=((((int) data[1])) & 0xff);
                            }else if (lenlen == 3){
                                recv+=((((int) data[0]) & 0xff) * 0x10000);
                                recv+=((((int) data[1]) & 0xff) * 0x100);
                                recv+=((((int) data[2]) & 0xff));
                            }else if (lenlen == 4){
                                recv+=((((int) data[0]) & 0xff) * 0x1000000);
                                recv+=((((int) data[1]) & 0xff) * 0x10000);
                                recv+=((((int) data[2]) & 0xff) * 0x100);
                                recv+=((((int) data[3]) & 0xff));
                            }
                        }
                    }
                    byte[]buffer = Remote.Read(recv);
                    byte ret=0;
                    if ( buffer != null ){
                        ret=0;
                    }else{
                        ret=12;
                    }

                    byte[]result=new byte[1];
                    Packet res=new Packet(Packet.Type.__MPOS_GET_DATA_RES);
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),msg.searchTlv(Tlv.Tags.__TAG_TERMINALID.get()).getData()));
                    if ( ret==0 && buffer != null ){
                        result[0]=ret;
                        res.tlvs.add(new Tlv(Tlv.Tags.__TAG_ISO8583.get(),buffer));
                    }else{
                        result[0]=ret;
                    }
                    res.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),result));
                    Send(res);
                    res=null;
                    buffer=null;
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (fError!=null){
                fError.setData(null);
                fError.run();
            }
        }
    }


    public void cleanClose(BluetoothSocket btSocket)
    {
        if(btSocket == null)
            return;

        if(Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT <= 20)
        {
            /*try { cleanCloseFix(btSocket); }
            catch (Exception e)
            {
                Log.d(TAG, "Exception during BluetoothSocket close bug fix: " + e.toString());
            }*/

            //Go on to call BluetoothSocket.close() too, because our code didn't do quite everything
        }

        //if(btSocket!=null) {
            //Call BluetoothSocket.close()
            try {
                //if(!btSocket.isConnected()) {
                    //Thread.sleep(1000);
                if(mmInStream!=null) {
                    mmInStream.close();
                }
                mmInStream = null;

                if(mmOutStream!=null){
                    mmOutStream.close();
                }
                mmOutStream = null;

                btSocket.close();
                //}
            } catch (Exception e) {
                Log.d(TAG, "Exception during BluetoothSocket close: " + e.toString());
            }
        //}

    }

    private static void cleanCloseFix(BluetoothSocket btSocket) throws IOException
    {
        synchronized(btSocket)
        {
            Field socketField = null;
            LocalSocket mSocket = null;
            try
            {
                socketField = btSocket.getClass().getDeclaredField("mSocket");
                socketField.setAccessible(true);

                mSocket = (LocalSocket)socketField.get(btSocket);
            }
            catch(Exception e)
            {
                Log.d(TAG, "Exception getting mSocket in cleanCloseFix(): " + e.toString());
            }

            if(mSocket != null)
            {
                try {
                //mSocket.shutdownInput();
                //mSocket.shutdownOutput();
                //mSocket.close();

                //mSocket = null;

                 socketField.set(btSocket, mSocket); }
                catch(Exception e)
                {
                    Log.d(TAG, "Exception setting mSocket = null in cleanCloseFix(): " + e.toString());
                }
            }


            Field pfdField = null;
            ParcelFileDescriptor mPfd = null;
            try
            {
                pfdField = btSocket.getClass().getDeclaredField("mPfd");
                pfdField.setAccessible(true);

                mPfd = (ParcelFileDescriptor)pfdField.get(btSocket);
            }
            catch(Exception e)
            {
                Log.d(TAG, "Exception getting mPfd in cleanCloseFix(): " + e.toString());
            }

            if(mPfd != null)
            {
                mPfd.close();

                mPfd = null;

                try { pfdField.set(btSocket, mPfd); }
                catch(Exception e)
                {
                    Log.d(TAG, "Exception setting mPfd = null in cleanCloseFix(): " + e.toString());
                }
            }

        } //synchronized
    }
}

