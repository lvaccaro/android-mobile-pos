package vaccarostudio.com.verifone;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Remote {

    private static final String TAG = "REMOTE";
    private static Socket socket = null;
    private static byte[] data = new byte[16000];
    public static boolean isOpen = false;
    public static int count = 0;
    private static boolean isFirstDataPkt = false;


    private static BufferedOutputStream dataOutputStream = null;
    private static BufferedInputStream dataInputStream = null;

    public static boolean Disconnect() {
        ;//Log.d(TAG, "Disconnection");
    	/*if(socket==null)
    		return true;
    	else
    		return socket.isConnected();    	*/
        return true;
    }

    public static boolean Connect() {
        final MComm myComm = MComm.getInstance();
        if (!myComm.isConnected()) {
            return false;
        }

        try {
            Log.e(TAG, Interface.gtAddress);
            InetAddress serverAddr = InetAddress.getByName(Interface.gtAddress);
            socket = new Socket(serverAddr, Interface.gtPort);
            socket.setSoTimeout(500);
            dataInputStream = new BufferedInputStream(socket.getInputStream());
            dataOutputStream = new BufferedOutputStream(socket.getOutputStream());
            Log.d(TAG, "Connection");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "No connection");
            return false;
        }

/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                Receive();

            }
        }).start();
*/

        return true;
    }

    public static boolean Send(byte[] out) {
        // Start timer for packets after the first 06
        if (socket == null || socket.isConnected() == false) {
            Log.d(TAG + " OUT", "Socket error");
            return false;
        }
        try {
            dataOutputStream.write(out);
            dataOutputStream.flush();

            String str = "";
            for (int i = 0; i < out.length; i++)
                str += String.format("%02x", out[i]);
            Log.d(TAG + " OUT", str);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG + " OUT", "Write error");
            return false;
        }
        return true;
    }

    static synchronized byte[] Read(int size){
        byte[] buffer=null;
        try {
            /*int x=socket.getInputStream().available();
            if (x>0)
                Log.d(TAG+" IN",String.valueOf(x));
            int recv=dataInputStream.available();
            if (size > recv)
                dim=recv;
            else
                dim=size;*/

            buffer=new byte[size];
            int recv=dataInputStream.read(buffer,0,size);
            if(recv>0){
                String str="";
                str+= String.format("[%d]", size);
                for (int i = 0; i < recv; i++)
                    str += String.format("%02x", buffer[i]);
                Log.d(TAG+" IN", str);
            }else
                Log.d(TAG+" IN","Read 0");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG + " IN", "Read error");
            return null;
        }
        return buffer;
    }
/*    
    static byte[] Read(int size){
    	int dim=0;
    	if (size > count){
	    	dim=count;
	    	byte [] buffer= new byte[dim];
	    	System.arraycopy(data, 0, buffer, 0, dim);
	    	count=0;
	    	
   		 	String str = String.format("[%d]", size);
	        for (int i = 0; i < dim; i++)
	            str += String.format("%02x", buffer[i]);
	        str+=" -> QUEUE: ";       
	        ;//Log.d(TAG+" FLUSH", str);
	    	return buffer;
    	}else{
    		dim=size;
        	byte [] buffer= new byte[dim];
        	System.arraycopy(data, 0, buffer, 0, dim);
        	System.arraycopy(data, dim, data, 0, count-dim);
    		count-=size;
    		
    		
   		 	String str = String.format("[%d]", size);
	        for (int i = 0; i < dim; i++)
	            str += String.format("%02x", buffer[i]);
	        str+=" -> QUEUE: ";   
	        for (int i = 0; i < count; i++)
	            str += String.format("%02x", data[i]);
	        ;//Log.d(TAG+" FLUSH", str);
	        
        	return buffer;
    	}
    }

    static synchronized void Receive()
    {
    
    	 if (socket == null || socket.isConnected()==false){
         	;//Log.d(TAG+" IN", "Socket error");
         	return ;
         }
    	 byte[] buffer=new byte[4000];
    	 
    	 while(true){
	    	 try
	    	 {
	    		 if( dataInputStream.available() > 0 ){
		    		 int recv=dataInputStream.read(buffer,0,buffer.length);
		    		 if (recv>0){
		    		    	System.arraycopy(buffer, 0, data, count, recv);
			    			count+=recv;
	
			    			String str="";
			    			str+=String.format("[%d]", count);
						    for (int i = 0; i < count; i++)
						       str += String.format("%02x", data[i]);
						    ;//Log.d(TAG+" IN", str);
		    		 } else{
		    			;//Log.d(TAG+" IN","Read 0");
		    		 }
	    		 }
		     }
		     catch (Exception e)
		     {
		        	;//Log.d(TAG+" IN","Read error");
		        	return ;
		     }
    	 }
	        
    }
    */

    static synchronized void Receive() {

/*
        int data;
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            while ((data = dataInputStream.read()) != -1)
            {

                Log.d(TAG+" IN",String.valueOf(data));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        int data;
        byte[] buffer=new byte[4000];
        try {

            while (true)
            {

                data = dataInputStream.available();
                if (data>0) {
                    Log.d(TAG + " AVAILABLE", String.valueOf(data));


                    data = dataInputStream.read(buffer, 0, buffer.length);
                    if (data > 0)
                        Log.d(TAG + " DATA", String.valueOf(data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*while (true)
            {
                int recv=dataInputStream.available();
                if (recv>0) {
                    data=dataInputStream.read(buffer,0,buffer.length);
                    Log.d(TAG + " IN", String.valueOf(data));
                }
            }*/
//read data from server


    }
    /*
    static synchronized void Receive()
    {
        while(true)
        {
	        try
	        {
	            if (!socket.isConnected()){
	            	;//Log.d(TAG+" IN", "Socket error");
	            	return;
	            }
	
		        int recv=dataInputStream.read(data,0,data.length);
		        if (recv>0){
			        String str = "";
			        for (int i = 0; i < recv; i++)
			            str += String.format("%02x ", data[i]);
			        ;//Log.d(TAG+" IN", String.valueOf(recv));
			        
			        byte[]user_id_mobile_payment_system={ 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30, 0x30 , 0x30 , 0x30 , 0x30 , 0x30};
			        byte []terminalid={0x39,0x39,0x39,0x39,0x39,0x39,0x35,0x32};
			        byte[]result={0x00};
			        
					Packet msg=new Packet(Packet.__MPOS_SEND_DATA_RES);
					msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
			        msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),terminalid));
			        
			        byte []buffer=new byte[recv];
			        System.arraycopy(data, 0, buffer, 0, recv);
			        msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_ISO8583.get(),buffer));
			        msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_OPERATION_RESULT.get(),result));
			        
			        final MPayComm myComm = MPayComm.getInstance();
					if (!myComm.isConnected())
			        { 
			        	return;
			        }
			        myComm.send(msg);
		        }
	        }
	        catch (Exception e)
	        {
	        	;//Log.d(TAG+" IN","Read error");
	        	return;
	        }
        }
    }*/

}
