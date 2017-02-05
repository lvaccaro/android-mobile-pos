package vaccarostudio.com.verifone;

import java.util.ArrayList;
import java.util.List;

public class Tlv {

    public enum Tags{
        __TAG_STATE_GT1 ("TAG_STATE_GT1", 					0xBF868100),
        __TAG_STATE_GT2 ("TAG_STATE_GT2", 					0xBF868101),
        __TAG_STATE_GT3 ("TAG_STATE_GT3", 					0xBF868102),
        __TAG_STATE_GT4 ("TAG_STATE_GT4", 					0xBF868103),
        __TAG_STATE_GT5 ("TAG_STATE_GT5", 					0xBF868104),

        __TAG_RELEASE_SW_CB2 ("TAG_RELEASE_SW_CB2", 		0x0000DF05),
        __TAG_GTCODE ("TAG_GTCODE", 						0x0000DF11),
        __TAG_NETWORKTYPE ("TAG_NETWORKTYPE", 				0x0000DF2A),
        __TAG_ACQUIRER_NAME ("TAG_ACQUIRER_NAME", 			0x0000DF38),
        __TAG_NETWORKPAR ("TAG_NETWORKPAR", 				0x0000DF53),
        __TAG_IPPORT ("TAG_IPPORT", 						0x0000DF56),

        __TAG_PAN ("TAG_PAN", 								0x0000005A),
        __TAG_DATA ("TAG_DATA", 							0x0000009A),

        __TAG_CURRENCY ("TAG_CURRENCY", 					0x00005F2A),
        __TAG_PAN_SEQ ("TAG_PAN_SEQ", 						0x00005F34),

        __TAG_ACQUIRER_ID ("TAG_ACQUIRER_ID", 				0x00009F01),
        __TAG_AMOUNT ("TAG_AMOUNT", 						0x00009F02),
        __TAG_APPLICATION_ID ("TAG_APPLICATION_ID",			0x00009F06),
        __TAG_MERCHANT_ID ("TAG_MERCHANT_ID", 				0x00009F16),
        __TAG_TIME ("TAG_TIME", 							0x00009F21),
        __TAG_STAN ("TAG_STAN", 							0x00009F41),

        __TAG_CONNECTION1 ("TAG_CONNECTION1", 				0x0000FF04),
        __TAG_CONNECTION ("TAG_CONNECTION", 				0x0000FF05),

        __TAG_TERMINALID ("TAG_TERMINALID", 				0x009F8201),
        __TAG_STATE_TID ("TAG_STATE_TID", 					0x009F8202),
        __TAG_BITMAP_GT1 ("TAG_BITMAP_GT1", 				0x009F8204),
        __TAG_BITMAP_GT2 ("TAG_BITMAP_GT2", 				0x009F8205),

        __TAG_TOTAL_FLAG ("TAG_TOTAL_FLAG", 				0x009F821F),
        __TAG_ADDITIONAL ("TAG_ADDITIONAL",                 0x00BF8604),


        __TAG_TOKEN_REQUEST("__TAG_TOKEN_REQUEST",          0x00DF8107),
        __TAG_TOKEN_RESULT("__TAG_TOKEN_RESULT",            0x00DF8D01),
        __TAG_ROUTING("__TAG_ROUTING",                      0x00DF8109),
        __TAG_SERVICE("__TAG_SERVICE",                      0x0000DF6A),
        __TAG_EXPIRATION_DATE ("__TAG_EXPIRATION_DATE", 	0x00005F24),
        __TAG_BIN_CARD("__TAG_BIN_CARD", 					0x009F861C),

        __TAG_AUTHORIZATION_CODE ("__TAG_AUTHORIZATION_CODE",0x00000089),
        __TAG_62 ("__TAG_62",                               0x009F861B),
        __TAG_NONLINE ("__TAG_NONLINE",                     0x009F861D),

        __TAG_USER_ID_MOBILE_PAYMENT_SYSTEM ("TAG_USER_ID_MOBILE_PAYMENT_SYSTEM", 0x009F8610),
        __TAG_OPERATION_RESULT ("TAG_OPERATION_RESULT", 	0x009F8611),
        __TAG_SPECIALTID ("TAG_SPECIALTID", 				0x009F8612),
        __TAG_TICKET ("TAG_TICKET", 						0x009F8613),
        __TAG_CLOSING_RESULT ("TAG_CLOSING_RESULT", 		0x009F8614),
        __TAG_TOTAL_HOST ("TAG_TOTAL_HOST", 				0x009F8615),
        __TAG_TOTAL_LOCAL ("TAG_TOTAL_LOCAL",	 			0x009F8616),
        __TAG_TRANSACTION_ID ("TAG_TRANSACTION_ID",			0x009F8617),
        __TAG_PAYMENT_TYPE ("TAG_PAYMENT_TYPE",				0x009F8618),
        __TAG_FINANCE_RESULT ("TAG_FINANCE_RESULT",			0x009F861A),
        __TAG_TOTAL_TYPE ("TAG_TOTAL_TYPE",					0x009F861F),
        __TAG_ISO8583 ("TAG_ISO8583", 						0x009F8620),
        __TAG_PRINT_MESSAGE ("TAG_PRINT_MESSAGE",			0x009F8621),
        __TAG_READ_MAX_BYTES ("TAG_READ_MAX_BYTES",			0x009F8622),
        __TAG_DLL_TYPE ("TAG_DLL_TYPE",						0x009F8623),
        __TAG_MENU_TYPE ("TAG_MENU_TYPE",					0x009F8624),
        __TAG_STATE_BITMAP ("TAG_STATE_BITMAP",		        0x009F8625),
        __TAG_FILESIZE ("TAG_FILESIZE",						0x009F8632),
        __TAG_FILE ("TAG_FILE",								0x009F8633),
        __TAG_LAST_PKT ("TAG_LAST_PKT",						0x009F8634),
        __TAG_AMOUNT_CASHBACK ("TAG_AMOUNT_CASHBACK",		0x009F8635),
        __TAG_PRODUCT_CODE ("TAG_PRODUCT_CODE", 			0x009F8636),
        __TAG_VERSION_CODE ("TAG_VERSION_CODE", 			0x009F8637),
        __TAG_LAST_PAGE ("TAG_LAST_PAGE", 					0x009F8638);



        private int value;
        private String name;

        Tags(String name, int value){
            this.value=value;
            this.name=name;
        }
        public int get(){
            return value;
        }
        @Override
        public String toString(){
            return name;
        }
    };


    private int iTag;
    private int iLen;
    private byte[] data;


    public int getType(){
        return iTag;
    }

    public byte[] getTag (  )
    {
        byte[] dst=new byte[getTagSize()];
        int iTagLen = getTagSize();

        for (int i = 0; i < iTagLen; i++)
        {
            dst[i] = (byte)(iTag >> (8 * (iTagLen - (i+1))));
        }
        return dst;
    }

    private int getTagSize ()
    {
        int x=(iTag & 0xff000000);
        if ((iTag & 0xff000000) != 0x00000000)
            return 4;
        else if ((iTag & 0xffff0000) != 0x00000000)
            return 3;
        else if ((iTag & 0xffffff00) != 0x00000000)
            return 2;
        return 1;
    }

    public void setDataLength(int len){
        iLen=0;
        if (len < 0x80 ){
            iLen=len;
        }else if ((len &0xfff00000) != 0x00000000){
            iLen +=     0x83000000;
            iLen += len&0x00ffffff;
        }else if ((len &0xffff0000) != 0x00000000){
            iLen +=     0x83000000;
            iLen += len&0x000fffff;
        }else if ((len &0xfffff000) != 0x00000000){
            iLen +=     0x00820000;
            iLen += len&0x0000ffff;
        }else if ((len &0xffffff00) != 0x00000000){
            iLen +=     0x00820000;
            iLen += len&0x00000fff;
        }else if ((len &0xfffffff0) != 0x00000000){
            iLen +=     0x00008100;
            iLen += len&0x000000ff;
        }else {
            iLen += 0x00008100;
            iLen += len&0x0000000f;
        }

    }

    public int getDataLength(){
        int lenlen=getLenSize();
        int dataLength=0;
        if (lenlen == 1){
            dataLength += iLen & 0x000000ff;
        }else if (lenlen == 2){
            dataLength += iLen & 0x000000ff;
        }else if (lenlen == 3){
            dataLength += iLen & 0x0000ffff;
        }else if (lenlen == 4){
            dataLength += iLen & 0x00ffffff;
        }else
            return -1;
        return dataLength;
    }

    public byte[] getLen (  )
    {
        int lenlen=getLenSize ();
        byte [] dst=new byte[lenlen];

        if (lenlen==1){
            dst[0]=(byte)(iLen & 0xff);
        }else if (lenlen==2){
            dst[0]=(byte) (0x81);
            dst[1]=(byte)(iLen & 0xff);
        }else if (lenlen==3){
            dst[0]=(byte) (0x82);
            dst[1]=(byte)((iLen >>> 8) & 0xff);
            dst[2]=(byte)((iLen) & 0xff);
        }else if (lenlen==4){
            dst[0]=(byte) (0x83);
            dst[1]=(byte)((iLen >>> 16) & 0xff);
            dst[2]=(byte)((iLen >>> 8) & 0xff);
            dst[3]=(byte)((iLen) & 0xff);
        }else if (lenlen==5){
            dst[0]=(byte) (0x84);
            dst[1]=(byte)((iLen >>> 24) & 0xff);
            dst[2]=(byte)((iLen >>> 16) & 0xff);
            dst[3]=(byte)((iLen >>> 8) & 0xff);
            dst[4]=(byte)((iLen) & 0xff);
        }
        else
            return null;
        return dst;
    }

    private int getLenSize ()
    {
        if ((iLen & 0xffffff00) == 0x00000000){
            return 1;
        } else if ( (iLen & 0xffff0000) == 0x00000000 ) {
            return 2;
        } else if ( (iLen & 0xff000000) == 0x00000000 ) {
            return 3;
        } else
            return 4;
    }

    public byte[] getData ( )
    {
        if (iLen==0)
            return null;
        byte[] out = new byte[getDataLength()];
        System.arraycopy(data, 0, out, 0, getDataLength());
        return out;
    }
	/*public int setData ( byte[] raw , int pos, int len)
	{
		iLen = len;
		data=new byte[len];
		System.arraycopy(raw, pos, data, 0, len);
		return 0;
	}*/

    public int size(){
        if (data==null)
            return getTagSize()+getLenSize();
        return getTagSize()+getLenSize()+data.length;
    }

    // TLV building interface

    public Tlv(int tag, byte[] data )
    {
        iTag = tag;
        setDataLength(data.length);
        this.data=data.clone();
    }

    public byte[] toBytes ( )
    {
        int iTotLen = getTagSize() + getLenSize() + data.length;
        byte[] toRet = new byte[iTotLen];
        byte []tag=getTag();
        byte []len=getLen();
        System.arraycopy(tag,0,toRet,0,tag.length);
        System.arraycopy(len,0,toRet,getTagSize(),len.length);
        System.arraycopy(data,0,toRet,getTagSize()+getLenSize(),data.length);
        return toRet;
    }
    public byte[]get(){
        return toBytes();
    }

    // TLV parsing interface
    public Tlv(byte[] raw)
    {

        int curIndx = 0;
        // parse tag
        iTag=0;

        if (raw[curIndx] == (byte)0x5A || raw[curIndx]==(byte)0x9A || raw[curIndx] == (byte)0x89){
            iTag += raw[curIndx] & 0x000000ff;
            curIndx++;
        }else if (raw[curIndx] == (byte)0x5F || raw[curIndx]==(byte)0xDF || raw[curIndx]==(byte)0xFF ||
                (raw[curIndx]==(byte)0x9F  && ( raw[curIndx+1]==(byte)0x01 || raw[curIndx+1]==(byte)0x02 ||raw[curIndx+1]==(byte)0x06 ||raw[curIndx+1]==(byte)0x16 || raw[curIndx+1]==(byte)0x21 || raw[curIndx+1]==(byte)0x41))){
            iTag = raw[curIndx]*0x100 & 0x0000ff00;
            curIndx++;
            iTag += raw[curIndx] & 0x000000ff;
            curIndx++;
        }else if (raw[curIndx]==(byte)0x9F) {
            iTag += raw[curIndx]*0x10000 & 0x00ff0000;
            curIndx++;
            iTag += raw[curIndx]*0x100 & 0x0000ff00;
            curIndx++;
            iTag += raw[curIndx] & 0x000000ff;
            curIndx++;
        }else if (raw[curIndx]==(byte) 0xBF){
            iTag += raw[curIndx]*0x1000000 & 0xff000000;
            curIndx++;
            iTag += raw[curIndx]*0x10000 & 0x00ff0000;
            curIndx++;
            iTag += raw[curIndx]*0x100 & 0x0000ff00;
            curIndx++;
            iTag += raw[curIndx] & 0x000000ff;
            curIndx++;
        }else
            return;

        //parse len
        if ((raw[curIndx] & 0x80)==(byte) 0x00){
            // single
            iLen += raw[curIndx] & 0x000000ff;
            curIndx++;
        }else{
            // start with 0x80
            byte lenlen = (byte) (raw[curIndx] & 0x7F);
            if (lenlen == 1){
                iLen += 0x00008100;
                curIndx++;
                iLen += raw[curIndx] & 0x000000ff;
                curIndx++;
            }else if (lenlen == 2){
                iLen += 0x00820000;
                curIndx++;
                iLen += raw[curIndx] *0x100 & 0x0000ff00;
                curIndx++;
                iLen += raw[curIndx] & 0x000000ff;
                curIndx++;
            }else if (lenlen==3){
                iLen += 0x83000000;
                curIndx++;
                iLen += raw[curIndx] *0x10000 & 0x00ff0000;
                curIndx++;
                iLen += raw[curIndx] *0x100 & 0x0000ff00;
                curIndx++;
                iLen += raw[curIndx] & 0x000000ff;
                curIndx++;
            }
        }
        //parse data
        data = new byte[getDataLength()];
        System.arraycopy(raw, curIndx, data, 0, getDataLength());
		
		/*
		if ((raw[curIndx] & 0x1F) == 0x1F)
		{
			// first byte of tag shall have 0x1F set
			iTag = raw[curIndx];
			curIndx++;
			
			// get second byte of tag
			iTag *= 16;
			iTag += raw[curIndx];
			
			// get third and so on
			while ((raw[curIndx] & 0x80) == 0x80)
			{
				curIndx++;
				iTag *= 16;
				iTag += raw[curIndx];
			}
		}
		else
		{
			iTag = raw[curIndx];
			curIndx++;
		}
		
		// parse length
		if ((raw[curIndx] & 0x80) == 0x80)
		{
			iLenLen = 1 + (raw[curIndx] & 0x7F);
			iLen = 0;

			for (i = 1; i < iLenLen; i++)
			{
				iLen = (iLen * 16) + raw[curIndx];
				curIndx ++;
			}
		}
		else
		{
			iLenLen = 1;
			iLen = raw[curIndx];
			curIndx++;
		}

		data = new byte[iLen];
		System.arraycopy(raw, curIndx, data, 0, iLen);

		iDataObjectLen = (curIndx - 0) + iLen;*/
    }

    public String toString(){
        String str="";

        for (int i=0;i<getTagSize();i++)
            str+= String.format("%02x", getTag()[i]);
        if (str.length()<=6)
            str+="\t";
        str+="\t [";
        byte []length=getLen();
        for (int i=0;i<length.length;i++)
            str+= String.format("%02x", length[i]);
        str+="] \t= ";
        if (data!=null){
            for (int i=0;i<data.length;i++)
                str+= String.format("%02x",data[i]);
        }
        str+=" : ";
        for (Tags tag : Tags.values()) {
            if(tag.get() == iTag)  {
                str+=tag.toString();
                break;
            }
        }

        if (iTag== Tags.__TAG_OPERATION_RESULT.get()){
            byte error= getData()[0];
            List<String> errors= new ArrayList<String>();
            errors.add("SUCCESS");
            errors.add("NOT AUTHENTICATED");
            errors.add("ABORTED BY USER");
            errors.add("CB2 INTERNAL ERROR");
            errors.add("MISSING MANDATORY DATA");
            errors.add("INVALID TID");
            errors.add("INVALID STATUS");
            errors.add("INVALID PASSWORD");

            errors.add("CLOSURE FAILED");
            errors.add("JOURNAL ERROR");
            errors.add("AUTHENTICATION");
            errors.add("DOWNLOAD FAILED");
            errors.add("CONNECTION ERROR");
            errors.add("CONNECTION LOST");
            errors.add("HOST ABORT AAA");
            errors.add("HOST ABORT BBB");
            errors.add("HOST ABORT CCC");
            errors.add("LOG FULL");
            errors.add("NO LAST PAY DATA AVAILABLE");
            errors.add("OP NOT SUPPORTED");
            errors.add("USE MICROCHIP");
            errors.add("INVALID CARD DATA");
            errors.add("UNSUPPORTED CURRENCY");
            errors.add("REVERSAL NOT ALLOWED");
            errors.add("NO DISK SPACE");
            errors.add("BETTERY LEVEL");

            str += " - "+ errors.get( getData()[0] );
        }
        else if (iTag== Tags.__TAG_TICKET.get()){
            str += "\n TICKET:\n";
            byte[] ticket=getData().clone();
            int j=0;
            for (int i=0;i<ticket.length;i++){
                if (j>=0 && j<=3){
                    j++;
                } else if (ticket[i]=='\n'){
                    str+= String.format("%c", ticket[i]&0x000000ff);
                    j=0;
                } else {
                    str+= String.format("%c", ticket[i]&0x000000ff);
                    j++;
                }
            }
        }

        return str;
    }




}
