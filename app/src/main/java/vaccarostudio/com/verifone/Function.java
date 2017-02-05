package vaccarostudio.com.verifone;

/**
 * Created by lucavaccaro on 04/03/14.
 */
public class Function implements Runnable {
    Packet arg=null;
    byte [] bytes=null;
    @Override
    public void run(){}
    public void setData(Packet packet){arg=packet;}
    public Packet getData(){return arg;}
    public void setBytes(byte[] bytes){this.bytes=bytes;}
    public byte[] getBytes(){return bytes;}
};