package vaccarostudio.com.verifone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Packet {



    public enum Type{

        __MPOS_AUTHENTICATION_REQ("__MPOS_AUTHENTICATION_REQ",0x0001),
        __MPOS_AUTHENTICATION_RES("__MPOS_AUTHENTICATION_RES",0x8001),
        __MPOS_SEND_KEY_REQ("__MPOS_SEND_KEY_REQ",0x0002),
        __MPOS_SEND_KEY_RES("__MPOS_SEND_KEY_RES",0x8002),

        __MPOS_FIRST_DLL_REQ("__MPOS_FIRST_DLL_REQ",0x0003),
        __MPOS_FIRST_DLL_RES("__MPOS_FIRST_DLL_RES",0x8003),
        __MPOS_CLOSURE_REQ("__MPOS_CLOSURE_REQ",0x0004),
        __MPOS_CLOSURE_RES("__MPOS_CLOSURE_RES",0x8004),
        __MPOS_PAYMENT_REQ("__MPOS_PAYMENT_REQ",0x0005),
        __MPOS_PAYMENT_RES("__MPOS_PAYMENT_RES",0x8005),
        __MPOS_REVERSAL_REQ("__MPOS_REVERSAL_REQ",0x0007),
        __MPOS_REVERSAL_RES("__MPOS_REVERSAL_RES",0x8007),
        __MPOS_PAYMENT_RESULT_REQ("__MPOS_TRANSACTION_INTERRUPT_REQ",0x0008),
        __MPOS_MENU_REQ("__MPOS_MENU_REQ",0x0009),
        __MPOS_MENU_RES("__MPOS_MENU_RES",0x8009),
        __MPOS_STATE_REQ("__MPOS_STATE_REQ",0x000E),
        __MPOS_STATE_RES("__MPOS_STATE_RES",0x800E),
        __MPOS_CARD_INFO_REQ("__MPOS_CARD_INFO_REQ",0x0010),
        __MPOS_CARD_INFO_RES("__MPOS_CARD_INFO_RES",0x8010),
        __MPOS_TOTALS_REQ("__MPOS_TOTALS_REQ",0x0011),
        __MPOS_TOTALS_RES("__MPOS_TOTALS_RES",0x8011),
        __MPOS_LOG_REQ("__MPOS_LOG_REQ",0x0012),
        __MPOS_LOG_RES("__MPOS_LOG_RES",0x8012),
        __MPOS_UPDATE_REQ("__MPOS_UPDATE_REQ",0x0013),
        __MPOS_UPDATE_RES("__MPOS_UPDATE_RES",0x8013),
        __MPOS_DOWNLOAD_REQ("__MPOS_DOWNLOAD_REQ",0x0014),
        __MPOS_DOWNLOAD_RES("__MPOS_DOWNLOAD_RES",0x8014),
        __MPOS_UPLOAD_REQ("__MPOS_UPLOAD_REQ",0x0015),
        __MPOS_UPLOAD_RES("__MPOS_UPLOAD_RES",0x8015),
        __MPOS_AUTO_REQ("__MPOS_AUTO_REQ",0x0016),
        __MPOS_AUTO_RES("__MPOS_AUTO_RES",0x8016),

        __MPOS_END_SESSION_RES("__MPOS_END_SESSION_RES",0x0017),
        __MPOS_TICKET_NO_NOTIFY_RES("__MPOS_TICKET_NO_NOTIFY_RES",0x8018),
        __MPOS_TICKET_OFFLINE_RES("__MPOS_TICKET_OFFLINE_RES",0x8019),

        // messages originating from Dongle (D2xx)
        __MPOS_CONNECTION_REQ("__MPOS_CONNECTION_REQ",0x000A),
        __MPOS_CONNECTION_RES("__MPOS_CONNECTION_RES",0x800A),
        __MPOS_SEND_DATA_REQ("__MPOS_SEND_DATA_REQ",0x000B),
        __MPOS_SEND_DATA_RES("__MPOS_SEND_DATA_RES",0x800B),
        __MPOS_GET_DATA_REQ("__MPOS_GET_DATA_REQ",0x000C),
        __MPOS_GET_DATA_RES("__MPOS_GET_DATA_RES",0x800C),
        __MPOS_DISCONNECT_REQ("__MPOS_DISCONNECT_REQ",0x000D),
        __MPOS_DISCONNECT_RES("__MPOS_DISCONNECT_RES",0x800D),


        __MPOS_INFOCHIP_RES("__MPOS_INFOCHIP_RES",0x801A),

        __MPOS_ACQUIRER_REQ("__MPOS_ACQUIRER_REQ",0x001C),
        __MPOS_ACQUIRER_RES("__MPOS_ACQUIRER_RES",0x801C),

        __MPOS_ACK("__MPOS_ACK",0x0006),
        __MPOS_NAK("__MPOS_NAK",0x000F);

        String name;
        int value=0;
        Type(String name, int value) {
            this.name=name;
            this.value=value&0x0000ffff;
        }
        public int get(){
            return value;
        }
        public String toString(){
            return this.name;
        }
        static public Type valueOf(int value){
            for ( Type t: Type.values()){
                if (t.get()==value)
                    return t;
            }
            return null;
        }
    };


    protected Type iMsgType=null;
    protected byte iVersion =  0x01 ;
    protected byte iCounter=0; //1-255
    protected byte[] szMobDevID = null;//16
    protected byte[] szDongleID = null;//8
    protected int iBodyLen = 0;
    protected byte[] pBody=null;


    public List<Tlv> tlvs=new ArrayList<Tlv>();

    public int size() { return 32 + iBodyLen; }//32 + 6 + iBodyLen

    public Packet(){
        iCounter = 0;
        iBodyLen = 0;
        szMobDevID = new byte[16];
        szDongleID = new byte[8];
    }
    public Packet(Type iMsg)
    {
        iMsgType=iMsg;
        iCounter = 0;
        iBodyLen = 0;
        szMobDevID = new byte[16];
        szDongleID = new byte[8];

        byte [] defaultMobDevID = MComm.getInstance().getMobileDeviceID();// {'a', 'n', 'd', 'r', 'o', 'i', 'd','-', 't', 'e', 's', 't', '-', '0','0','0'};
        byte [] defaultDongleID = MComm.getInstance().getDongleID();//{'5', '0','0','0','1','8','7','4' };
        System.arraycopy(defaultMobDevID, 0, szMobDevID, 0, 16);
        System.arraycopy(defaultDongleID, 0, szDongleID, 0, 8);
    }

    public byte[] toBytes ( )
    {
        byte []tmp=null;
        int idx = 0;
        if (iBodyLen>0){
            tmp = new byte[size()];
            tmp[idx++] = (byte) ((iMsgType.get() >>> 8) & 0xff);
            tmp[idx++] = (byte) (iMsgType.get() & 0xff);
            tmp[idx++] = 1;
            tmp[idx++] = (byte) (iCounter & 0xff);
            System.arraycopy(szMobDevID, 0, tmp, idx, 16);
            idx+=16;
            System.arraycopy(szDongleID, 0, tmp, idx, 8);
            idx+=8;
            // data field is already set
            tmp[idx++] = (byte) ((iBodyLen >>> 24) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen >>> 16) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen >>> 8) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen) & 0xff);
            if (pBody!=null)
                System.arraycopy(pBody, 0, tmp, idx, iBodyLen);
        }else {
            iBodyLen=0;
            for (Tlv tlv : tlvs){
                iBodyLen+=tlv.toBytes().length;
            }
            tmp = new byte[size()];
            tmp[idx++] = (byte) ((iMsgType.get() >>> 8) & 0xff);
            tmp[idx++] = (byte) (iMsgType.get() & 0xff);
            tmp[idx++] = 1;
            tmp[idx++] = (byte) (iCounter & 0xff);
            System.arraycopy(szMobDevID, 0, tmp, idx, 16);
            idx+=16;
            System.arraycopy(szDongleID, 0, tmp, idx, 8);
            idx+=8;
            // data field is already set
            tmp[idx++] = (byte) ((iBodyLen >>> 24) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen >>> 16) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen >>> 8) & 0xff);
            tmp[idx++] = (byte) ((iBodyLen) & 0xff);
            //data field is not set -> take from tags list
            if (tlvs!=null){
                for (Tlv tlv : tlvs){
                    byte []xxx= tlv.toBytes();
                    System.arraycopy( xxx,0,tmp,idx, xxx.length);
                    idx+=tlv.toBytes().length;
                }
            }

        }

        return tmp;
    }
    public void setBody ( byte[] pData, int iDataLen )
    {
        pBody = pData;
        iBodyLen = iDataLen;
    }
    public byte[] getBody ( )
    {
        return pBody.clone();
    }

    public void setMessageType ( Type type )
    {
        iMsgType=type;
    }
    public Type getMessageType ( )
    {
        return iMsgType;
    }

    public void setCounter ( byte counter )
    {
        iCounter=counter;
    }
    public byte getCounter ( )
    {
        return iCounter;
    }


    public void setDongleID ( byte[] bDongle, int index )
    {
        if (((bDongle.length) - index) >= 8)
        {
            for (int i = 0; i < 8; i++)
                szDongleID[i] = (byte)(bDongle[i+index] & 0xff);
        }
    }

    public void setDongleID ( String szDongle )
    {
        char[] cDongle = szDongle.toCharArray();

        if (cDongle.length >= 8)
        {
            for (int i = 0; i < 8; i++)
                szDongleID[i] = (byte)(cDongle[i] & 0xff);
        }
    }

    public byte[] getDongleID(){
        return szDongleID.clone();
    }

    public void setMobileDeviceID ( byte[] bMobDevID, int index )
    {
        if (((bMobDevID.length) - index) >= 8)
        {
            for (int i = 0; i < 8; i++)
                szMobDevID[i] = (byte)(bMobDevID[i+index] & 0xff);
        }
    }

    public void setMobileDeviceID ( String strMobDevID )
    {
        char[] cMobDevID = strMobDevID.toCharArray();

        if (cMobDevID.length >= 8)
        {
            for (int i = 0; i < 8; i++)
                szMobDevID[i] = (byte)(cMobDevID[i] & 0xff);
        }
    }

    public byte[] getMobileDeviceID(){
        return szMobDevID.clone();
    }

    public Packet(byte[] raw)
    {
        //message type
        int cmd=((((int)(raw[0])) & 0xff) * 0x100) + (((int) raw[1]) & 0xff);
        iMsgType= Type.valueOf(cmd);

        //counter
        iCounter = raw[2];

        //mobile device id
        szMobDevID = new byte[16];
        System.arraycopy(raw, 4, szMobDevID, 0, 16);

        //dongle id
        szDongleID = new byte[8];
        System.arraycopy(raw, 20, szDongleID, 0, 8);

        //Body
        iBodyLen = 0;
        iBodyLen+=((((int)(raw[28])) & 0xff) * 0x1000000);
        iBodyLen+=((((int)(raw[29])) & 0xff) * 0x10000);
        iBodyLen+=((((int)(raw[30])) & 0xff) * 0x100);
        iBodyLen+=(((int) raw[31]) & 0xff);
        if (iBodyLen > 0)
        {
            pBody = new byte[iBodyLen];
            System.arraycopy(raw, 32, pBody, 0, iBodyLen);
            //Tags
            int count=0;
            while(count<this.getBody().length){
                Tlv tlv= new Tlv( Arrays.copyOfRange(pBody, count, pBody.length ) );
                count+=tlv.size();
                tlvs.add(tlv);
            }
        }
    }
    public String toString(){
        String str="";
        byte[] buffer=this.toBytes();
        for (int i=0;i<buffer.length;i++)
            str+= String.format("%02x ", buffer[i]);
        return str;
    }
    public synchronized String toFormatString(){
        if (iMsgType.toString().equals("__MPOS_PAYMENT_RES")){
            ;
        }
        String str="";
        str+= String.format("\n>tag: %04x : %s",iMsgType.get(),iMsgType.toString());
        str+= String.format("\n>version: %d",iVersion);
        str+= String.format("\n>counter: %d",iCounter);
        str+= String.format("\n>szMobDevID: ");
        for (int i=0;i<szMobDevID.length;i++)
            str+= String.format("%c", szMobDevID[i]);
        str+= String.format("\n>szDongleID: ");
        for (int i=0;i<szDongleID.length;i++)
            str+= String.format("%c", szDongleID[i]);
        str+= String.format("\n>BodyLen: %d\n",iBodyLen);
        str+= String.format("\n>Body: ");
    		/*if (this.pBody!=null){
    			for (int i=0;i<pBody.length;i++)
    				str+=String.format("%02x ", pBody[i]);
    		}*/
        str+= String.format("\n");

        for (Tlv tlv: tlvs){
            str+=">"+tlv.toString()+"\n";

            //print inner tlv
            if (tlv.getType()==Tlv.Tags.__TAG_STATE_GT1.get() || tlv.getType()==Tlv.Tags.__TAG_CONNECTION.get()){
                int count=0;
                int size=tlv.getData().length;
                while(count<size){
                    Tlv tlv_= new Tlv( Arrays.copyOfRange(tlv.getData(), count, size ) );
                    count+=tlv_.size();
                    str+="> >>"+tlv_.toString()+"\n";
                }
            }
        }

        return str;
    }

    public Tlv searchTlv(int type){
        for (Tlv tlv: tlvs){
            if (type==tlv.getType())
                return tlv;
            if (tlv.getType()==Tlv.Tags.__TAG_STATE_GT1.get() || tlv.getType()==Tlv.Tags.__TAG_CONNECTION.get()){
                int count=0;
                int size=tlv.getData().length;
                while(count<size){
                    Tlv tlv_= new Tlv( Arrays.copyOfRange(tlv.getData(), count, size ) );
                    count+=tlv_.size();
                    if (type==tlv_.getType())
                        return tlv_;
                }
            }
        }
        return null;
    }

}

