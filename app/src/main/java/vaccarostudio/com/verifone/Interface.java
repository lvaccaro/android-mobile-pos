package vaccarostudio.com.verifone;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucavaccaro on 21/02/14.
 */
public class Interface {

    // production
    public static String gtAddress = "5.10.93.150";
    public static int gtPort = 10501;
    public static byte []terminalid={(byte) 0x99,(byte) 0x99,(byte) 0x99, (byte)0x01};
    public static byte []gtcode=null;//{(byte) 0x08,(byte) 0x81,(byte) 0x05};
    //public static byte []ipport={0x35,0x2e,0x31,0x30,0x2e,0x39,0x33,0x2e,0x31,0x35,0x30,0x20,0x20,0x20,0x20,0x31,0x30,0x35,0x30,0x31 };
    public static byte []ipport={0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20 };
    private static boolean initialize=false;
    private static boolean protocol=true;

    public static byte []networktype={0x05,0x05,0x00};
    public static byte []networkpar_bt={0x00,0x00,0x00,0x00};
    public static byte []networkpar_head={0x00,0x00,0x01,0x00};

    public static byte []specialtid={0x40,0x00};
    public static byte []dlltype={0x01};
    public static byte []user_id_mobile_payment_system={ 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30 , 0x30, 0x30 , 0x30 , 0x30 , 0x30 , 0x31};
    private final static String mpaySzReqUuid = "00001101-0000-1000-8000-00805F9B34FB";

    public static byte []stan=null;

    public static boolean isOpen()
    {
        return MComm.getInstance().isConnected();
    }


    public static void Initialize(String ipaddress , String port, String mobileDeviceID, String gtCode, Boolean protocolHead, Function fTicket) throws Exception {
        if (ipaddress==null)
            throw new Exception("Parameter cannot be null");
        else if (port==null)
            throw new Exception("Parameter cannot be null");
        else if (mobileDeviceID==null)
            throw new Exception("Parameter cannot be null");
        else if (gtCode==null)
            throw new Exception("Parameter cannot be null");

        MComm myComm = MComm.getInstance();
        myComm.setUuid(mpaySzReqUuid);
        byte []string="12345678".getBytes();
        myComm.setMobileDeviceID(string);
        //myComm.setMobileDeviceID(mobileDeviceID.getBytes());
        myComm.fTicket=fTicket;
        gtAddress=ipaddress;
        gtPort= Integer.parseInt(port);
        gtcode=StringToHalfByte(gtCode);
        System.arraycopy( ipaddress.getBytes() ,0,ipport,0, ipaddress.length());
        System.arraycopy( port.getBytes() ,0, ipport, ipport.length-port.length(), port.length());
        protocol=protocolHead;
        initialize=true;
    }

    public static void Initialize(String ipaddress , String port, String mobileDeviceID, String gtCode, Boolean protocolHead, Function fTicket, Function fAuto) throws Exception {
        MComm myComm = MComm.getInstance();
        myComm.fAuto=fAuto;
        Initialize(ipaddress,port,mobileDeviceID,gtCode,protocolHead,fTicket);
    }

        static boolean bluetooth=true;
    public static void Open(final Function fResult,final Function fError, BluetoothDevice mChosenDevice) throws Exception
    {
        if (isOpen())
            throw new Exception("Socket already open");
        else if (bluetooth && mChosenDevice==null)
            throw new Exception("Parameter cannot be null");
        else if (fResult==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        MComm myComm = MComm.getInstance();
        if (bluetooth)
            myComm.setBTdevice(mChosenDevice);
        myComm.fResult=new Function(){
            public void run(){
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Packet pkt= this.getData();
                fResult.setData(pkt);
                fResult.run();
            }
        };
        myComm.fError=fError;
        myComm.Open();
    }

    public static void Close(final Function fResult,final Function fError) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket already close");
        else if (fResult==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        MComm myComm = MComm.getInstance();
        myComm.fResult=fResult;
        myComm.fError=fError;
        myComm.Close();
    }

    public static void Authentication(final Function fResult,final Function fError) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (fResult==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fResult= new Function(){
            @Override
            public void run(){
                if (this.getData()==null){
                    fError.run();
                }else {
                    Packet pkt = this.getData();
                    myComm.setDongleID(pkt.getDongleID());
                    fResult.setData(pkt);
                    fResult.run();
                }
            }
        };
        myComm.fError=fError;

        new Thread(new Runnable(){
            @Override
            public void run() {
                byte[]defaultDongleID = {'0', '0','0','0','0','0','0','0' };
                myComm.setDongleID(defaultDongleID);
                Packet msg=new Packet(Packet.Type.__MPOS_AUTHENTICATION_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                myComm.Send(msg);
            }
        }).start();
    }

    public static void State(final Function fResult,final Function fError) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (fResult==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fResult=fResult;
        myComm.fError=fError;
        new Thread(new Runnable(){
            @Override
            public void run() {
                Packet msg=new Packet(Packet.Type.__MPOS_STATE_REQ);
                byte []state_bitmap={(byte) 0x80};
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_STATE_BITMAP.get(),state_bitmap));
                myComm.Send(msg);
            }
        }).start();
    }

    public static void LastTicket(final Function fTicket,final Function fError) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (fTicket==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fTicket=fTicket;
        myComm.fResult=null;
        myComm.fError=fError;
        new Thread(new Runnable(){
            @Override
            public void run() {
                Packet msg=new Packet(Packet.Type.__MPOS_PAYMENT_RESULT_REQ);

                myComm.Send(msg);
            }
        }).start();
    }

    public static void Dll(final Function fResultOK, final Function fResultKO, final Function fError, final String terminalid) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (fResultOK==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultKO==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();

        myComm.fError=fError;
        myComm.fResult = new Function(){
            @Override
            public void run(){
                Packet pkt= this.getData();
                fError.setData(pkt);
                fResultOK.setData(pkt);
                fResultKO.setData(pkt);
                Tlv tlv =pkt.searchTlv(Tlv.Tags.__TAG_OPERATION_RESULT.get());
                if (tlv==null)
                    fError.run();
                else if (tlv.getData().length!=4)
                    fError.run();
                else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 )
                    fResultOK.run();
                else
                    fResultKO.run();
            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                byte [] tid_formatted=StringToHalfByte(terminalid);

                Packet msg=new Packet(Packet.Type.__MPOS_FIRST_DLL_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_DLL_TYPE.get(),dlltype));
                // 9F811F: se assente default GT 1
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),tid_formatted));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_GTCODE.get(),gtcode));
                // FF05 inner tlv
                List<Tlv> tlvs=new ArrayList<Tlv>();
                tlvs.add(new Tlv(Tlv.Tags.__TAG_IPPORT.get(),ipport));
                tlvs.add(new Tlv(Tlv.Tags.__TAG_NETWORKTYPE.get(),networktype));
                if (protocol==false)
                    tlvs.add(new Tlv(Tlv.Tags.__TAG_NETWORKPAR.get(),networkpar_bt));
                else
                    tlvs.add(new Tlv(Tlv.Tags.__TAG_NETWORKPAR.get(),networkpar_head));
                int size=0,idx=0;
                for (Tlv tlv : tlvs){
                    size+=tlv.size();
                }
                byte []data=new byte[size];
                for (Tlv tlv : tlvs){
                    byte []xxx= tlv.toBytes();
                    System.arraycopy( xxx,0,data,idx, xxx.length);
                    idx+=xxx.length;
                }
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_CONNECTION.get(),data));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_SPECIALTID.get(),specialtid));
                byte []bitmapgt1={0x00,0x00,0x10,0x01};
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_BITMAP_GT1.get(),bitmapgt1));
                byte []bitmapgt2={0x00,0x00,0x06,0x01};
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_BITMAP_GT2.get(),bitmapgt2));
                myComm.Send(msg);
            }
        }).start();
    }

    public static void Menu(final Function fResult, final Function fError,  final String terminalid) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (fResult==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");


        final MComm myComm = MComm.getInstance();
        myComm.fResult = fResult;
        myComm.fError=fError;

        new Thread(new Runnable(){
            @Override
            public void run() {
                byte [] tid_formatted=StringToHalfByte(terminalid);
                Packet msg=new Packet(Packet.Type.__MPOS_MENU_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),tid_formatted));
                myComm.Send(msg);
            }
        }).start();
    }

    public static void Closure(final Function fResultOK, final Function fResultKO, final Function fError, final String terminalid) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultOK==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultKO==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fError=fError;
        myComm.fResult = new Function(){
            @Override
            public void run () {
                Packet pkt= this.getData();
                fError.setData(pkt);
                fResultOK.setData(pkt);
                fResultKO.setData(pkt);
                Tlv tlv =pkt.searchTlv(Tlv.Tags.__TAG_OPERATION_RESULT.get());
                if (tlv==null)
                    fError.run();
                else if (tlv.getData().length!=4)
                    fError.run();
                else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 ) {
                    tlv =pkt.searchTlv(Tlv.Tags.__TAG_CLOSING_RESULT.get());
                    if (tlv==null)
                        fError.run();
                    else if (tlv.getData().length!=4)
                        fError.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 )
                        fResultOK.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==1 )
                        fResultOK.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==2 )
                        fResultKO.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==3 )
                        fResultKO.run();
                    else
                        fError.run();
                }else
                    fError.run();
            }
        };
        new Thread(new Runnable(){
            @Override
            public void run() {
                byte [] tid_formatted=StringToHalfByte(terminalid);
                Packet msg=new Packet(Packet.Type.__MPOS_CLOSURE_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),tid_formatted));
                myComm.Send(msg);
            }
        }).start();
    }
    public static void Totals(final Function fResultOK, final Function fResultKO, final Function fError, final String terminalid) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultOK==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultKO==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fError=fError;
        myComm.fResult= new Function(){
            @Override
            public void run () {
                Packet pkt= this.getData();
                fError.setData(pkt);
                fResultOK.setData(pkt);
                fResultKO.setData(pkt);
                Tlv tlv =pkt.searchTlv(Tlv.Tags.__TAG_OPERATION_RESULT.get());
                if (tlv==null)
                    fError.run();
                else if (tlv.getData().length!=4)
                    fError.run();
                else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 )
                    fResultOK.run();
                else
                    fResultKO.run();
            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                byte []tot={0x03};
                byte [] tid_formatted=StringToHalfByte(terminalid);
                Packet msg=new Packet(Packet.Type.__MPOS_TOTALS_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),tid_formatted));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TOTAL_FLAG.get(),tot));
                myComm.Send(msg);
            }
        }).start();
    }

    public static void Acquirer (byte []bytes){
        final MComm myComm = MComm.getInstance();
        myComm.fAcquirer=null;
        Packet msg=new Packet(Packet.Type.__MPOS_ACQUIRER_RES);
        Tlv tlv_routing=new Tlv(Tlv.Tags.__TAG_ROUTING.get(),bytes);
        Tlv tlv_additional=new Tlv(Tlv.Tags.__TAG_ADDITIONAL.get(),tlv_routing.toBytes());
        msg.tlvs.add(tlv_additional);
        myComm.Send(msg);
    }
    public static void Payment(final Function fResultOK, final Function fResultKO, final Function fError, final Function fAcquirer, final String terminalid, final String amount, final boolean automatic, final boolean multiacquiring, final boolean tokenization) throws Exception
    {
        if (!isOpen())
            throw new Exception("Close connection");
        else if (amount==null)
            throw new Exception("Parameter cannot be null");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultOK==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultKO==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fError=fError;
        myComm.fAcquirer=fAcquirer;
        myComm.fResult = new Function(){
            @Override
            public void run () {
                if (this.getData()==null) {
                    fError.run();
                    return;
                }
                Packet pkt= this.getData();
                fError.setData(pkt);
                fResultOK.setData(pkt);
                fResultKO.setData(pkt);
                Tlv tlv =pkt.searchTlv(Tlv.Tags.__TAG_OPERATION_RESULT.get());
                if (tlv==null)
                    fError.run();
                else if (tlv.getData().length!=4)
                    fError.run();
                else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 ) {

                    tlv =pkt.searchTlv(Tlv.Tags.__TAG_FINANCE_RESULT.get());
                    if (tlv==null)
                        fError.run();
                    else if (tlv.getData().length!=4)
                        fError.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 )
                        fResultKO.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==1 )
                        fResultOK.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==2 )
                        fResultKO.run();
                    else
                        fResultKO.run();
                }else
                    fResultKO.run();

            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                byte [] tid_formatted=StringToHalfByte(terminalid);
                byte [] amount_formatted=StringToHalfByte(amount,6);
                byte [] xxx= {0x01};
                byte [] paymenttype=new byte[2];
                if (automatic==true) {
                    paymenttype[0] = (byte) 0xec;
                    paymenttype[1] = (byte) 0x00;
                }else{
                    paymenttype[0] = (byte) 0x1c;
                    paymenttype[1] = (byte) 0x00;
                }
                //enable acquirer choice
                if (multiacquiring)
                    paymenttype[1]|=0x40;


                Packet msg=new Packet(Packet.Type.__MPOS_PAYMENT_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),tid_formatted));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TRANSACTION_ID.get(), xxx ));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_PAYMENT_TYPE.get(),paymenttype));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_AMOUNT.get(),  amount_formatted));
                //byte[]additional={'H','e','l','l','o',' ','W','o','r','l','d','!'};
                //msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_ADDITIONAL.get(),  additional));

                // Service
                //byte []yyy=new byte[]{0x00,0x02};
                //msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_SERVICE.get(),yyy));

                // Tokenization enable
                if (tokenization) {
                    byte[] token_enable = new byte[]{0x01};
                    Tlv token = new Tlv(Tlv.Tags.__TAG_TOKEN_REQUEST.get(), token_enable);
                    Tlv additional = new Tlv(Tlv.Tags.__TAG_ADDITIONAL.get(), token.toBytes());
                    msg.tlvs.add(additional);
                }
                // Routing
                //byte []routing=new byte[]{0x20,0x20};
                //msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_PAYMENT_TYPE.get(),routing));


                myComm.Send(msg);
            }
        }).start();
    }

    public static void Chargeoff(final Function fResultOK, final Function fResultKO, final Function fError, final String terminalid, final String amount) throws Exception
    {
        if (!isOpen())
            throw new Exception("Socket close");
        else if (amount==null)
            throw new Exception("Parameter cannot be null");
        else if (terminalid==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultOK==null)
            throw new Exception("Parameter cannot be null");
        else if (fResultKO==null)
            throw new Exception("Parameter cannot be null");
        else if (fError==null)
            throw new Exception("Parameter cannot be null");
        else if (initialize==false)
            throw new Exception("No initialize");

        final MComm myComm = MComm.getInstance();
        myComm.fError=fError;
        myComm.fResult = new Function(){
            @Override
            public void run () {
                if (this.getData()==null) {
                    fError.run();
                    return;
                }
                Packet pkt= this.getData();
                fError.setData(pkt);
                fResultOK.setData(pkt);
                fResultKO.setData(pkt);
                Tlv tlv =pkt.searchTlv(Tlv.Tags.__TAG_OPERATION_RESULT.get());
                if (tlv==null)
                    fError.run();
                else if (tlv.getData().length!=4)
                    fError.run();
                else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 ) {
                    tlv =pkt.searchTlv(Tlv.Tags.__TAG_FINANCE_RESULT.get());
                    if (tlv==null)
                        fError.run();
                    else if (tlv.getData().length!=4)
                        fError.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==0 )
                        fResultKO.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==1 )
                        fResultOK.run();
                    else if (tlv.getData()[0]==0 && tlv.getData()[1]==0 && tlv.getData()[2]==0 && tlv.getData()[3]==2 )
                        fResultKO.run();
                    else
                        fResultKO.run();
                }else
                    fResultKO.run();
            }
        };
        new Thread(new Runnable(){
            @Override
            public void run() {
                byte [] xxx= {0x01};
                Packet msg=new Packet(Packet.Type.__MPOS_REVERSAL_REQ);
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_USER_ID_MOBILE_PAYMENT_SYSTEM.get(),user_id_mobile_payment_system));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TERMINALID.get(),StringToHalfByte(terminalid)));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_TRANSACTION_ID.get(), xxx ));
                msg.tlvs.add(new Tlv(Tlv.Tags.__TAG_AMOUNT.get(),  StringToHalfByte(amount,6) ));
                myComm.Send(msg);
            }
        }).start();
    }

    private static String HalfByteToString(byte[] tid){
        String str="";
        for (int i=0;i<tid.length;i++){
            str += String.format("%c", (byte) (((tid[i]&0xfffffff0)/0x10) &0x0000000f) + 0x30 );
            str += String.format("%c", (byte) ((tid[i] % 0x10) & 0x0000000f) + 0x30);
        }
        return str;
    }

    private static byte[] StringToHalfByte(String str){
        byte []buffer=new byte[str.length()/2];
        for (int i=0,j=0;i<buffer.length;i++,j+=2){
            buffer[i] = (byte)(((str.charAt(j)   - 0x30) & 0x0000000f) * 0x10);
            buffer[i] += (byte)(((str.charAt(j+1) - 0x30) & 0x0000000f)) ;
        }
        return buffer;
    }
    private static byte[] StringToHalfByte(String str, int size){
        while(size*2>str.length())
            str="0"+str;
        byte []buffer=new byte[str.length()/2];
        for (int i=0,j=0;i<buffer.length;i++,j+=2){
            buffer[i] = (byte)(((str.charAt(j)   - 0x30) & 0x0000000f) * 0x10);
            buffer[i] += (byte)(((str.charAt(j+1) - 0x30) & 0x0000000f)) ;
        }
        return buffer;
    }

    public static String Ticket_Screen(byte []raw)throws Exception {
        if (raw==null)
            throw new Exception("Parameter cannot be null");
        return Ticket.Ticket4Screen(raw);
    }
    public static List<Byte> Ticket_Printer(byte []raw)throws Exception {
        if (raw==null)
            throw new Exception("Parameter cannot be null");
        return Ticket.Ticket4Printer(raw);
    }

}
