package vaccarostudio.com.verifone;


import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vaccarostudio.com.usb.CdcAcmSerialDriver;
import vaccarostudio.com.usb.SerialInputOutputManager;
import vaccarostudio.com.usb.UsbSerialDriver;


public class SerialIO {
	public int i ;
	
	private static final String TAG = SerialIO.class.getSimpleName();
	private static UsbDeviceConnection mConnection;
    private static ExecutorService mExecutor;
    private static SerialInputOutputManager mSerialIoManager;
    private static CdcAcmSerialDriver driver;
    
    protected static UsbDevice device=null;
    protected static UsbManager mUsbManager=null;

    public static Handler handlerCallback=null;
    
    private static SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception e) {
            ;//Log.d(TAG, "Runner stopped.");
        }
        @Override
        public void onNewData(final byte[] data) {
            String output="IN:";
        	for (int i=0;i<data.length;i++)
                output+=String.format("%02x",data[i]);
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
    };
    
    public static void Close() throws Exception
    {

        try{
            mExecutor.shutdown();
            mSerialIoManager.stop();
            driver.close();
            mConnection.close();
            mExecutor=null;
            mSerialIoManager=null;
            driver=null;
            mConnection=null;
        }catch(Exception e){
            Log.d(TAG,"Close error");
        }
    }
    
    public static void Set(UsbManager mUsbManager, UsbDevice device) throws Exception
    {
   	 	if (device==null || mUsbManager==null)
			throw new Exception("No device found");
   	 	SerialIO.mUsbManager=mUsbManager;
   	 	SerialIO.device=device;
    }
		
    public static void Open() throws Exception
    {
   	 	if (device==null || mUsbManager==null)
			throw new Exception("No device found");
   	 	
   	 	mConnection = mUsbManager.openDevice(device);	
   	 	if (mConnection==null)
			throw new Exception("No device open");
		
   	 	driver = new CdcAcmSerialDriver(device, mConnection);
		if (driver==null)
			throw new Exception("No driver found");
		
		try {
			driver.open();
			driver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			driver.setDTR(true);
		        
		    mSerialIoManager = new SerialInputOutputManager(driver, mListener);
            mExecutor= Executors.newSingleThreadExecutor();
		    mExecutor.submit(mSerialIoManager);

		}catch (Exception e) {
		    Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
		    throw new Exception("Error setting up device: " + e.getMessage());
		}
    }
    
    public static void Send(byte []data) throws Exception {
    	try{
    		mSerialIoManager.writeAsync(data);

    	}catch(Exception e) {
		    Log.e(TAG, "Error writing: " + e.getMessage(), e);
		    throw new Exception("Error writing: " + e.getMessage());
		}
    }

    public static byte []buffer=new byte[1024];

    public static void Read() throws Exception {
        try{
            driver.read(buffer,1000);

        }catch(Exception e) {
            Log.e(TAG, "Error reading: " + e.getMessage(), e);
            throw new Exception("Error reading: " + e.getMessage());
        }
    }
    
}
