package vaccarostudio.com.verifone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucavaccaro on 28/04/14.
 */
public class Ticket {


    static byte ALIGN_CENTER=0x01;
    static byte ALIGN_LEFT=0x02;
    static byte FONT_TITLE=0x08;
    static byte FONT_STANDARD=0x10;
    static byte FONT_COLLAPSED=0x20;
    static byte PROPERTY_CLIENT=0x40;

    static int PROPERTY_MERCHANT=0x80;
    static int PROPERTY_SIGNATURE=0x1000;
    static int KIND_PAYMENT=0x0100;
    static int KIND_CHIPData=0x0200;


    public static String Ticket4Screen(byte[] ticket)
    {
        String stringTicket = "";

        // Lo scontrino che dovrò stampare
        List<Byte> parsedTicket = new ArrayList<Byte>();

        // L'insieme di righe che compongono lo scontrino (i primi 4 bytes sono di formattazione)
        List<List<Byte>> splittedTicket = _splitTicket(ticket);

        for ( List<Byte> row : splittedTicket)
        {
            // Dati di formattazione
            byte format = (byte) (row.get(0) | (row.get(1) << 8) | (row.get(2) << 16) | (row.get(3) << 24));

            // Rimuovo i byte di formattazione
            row.remove(0);
            row.remove(0);
            row.remove(0);
            row.remove(0);

                if (row.contains( Byte.valueOf((byte) 0xA4) )) // 164 ---> ¤
                {
                    int index = row.indexOf(Byte.valueOf((byte) 0xA4));
                    row.set(index - 1, (byte)' ');
                    row.set(index + 0, (byte)'E');
                    row.set(index + 1, (byte)'u');
                    row.set(index + 2, (byte)'r');
                    row.set(index + 3, (byte)'o');
                }

                // Sostituisco "û" [dovrebbe essere -] con "-".
                if (row.contains(Byte.valueOf((byte) 150) )) // 150 ---> û
                {
                    row.set(row.indexOf(Byte.valueOf((byte) 150)), (byte) 45); // 45 ---> -
                }

            // Se la riga è per il cliente, la cancello
            if ((format & (byte)PROPERTY_CLIENT) != 0)
            {
                // Se la riga deve apparire solo nello scontrino, non la aggiungo
                continue;
            }

            // Analizzo l'allineamento
            if ((format & (byte)ALIGN_CENTER) != 0)
            {
                // Testo centrato
                if (row.size() < 24)
                {
                    List<Byte> spaces = _getSpaces((24 - row.size()) / 2);
                    parsedTicket.addAll(spaces);
                    parsedTicket.addAll(row);
                    //parsedTicket.addAll(spaces);
                }
                else
                {
                    parsedTicket.addAll(row);
                }
            }
            else if ((format & (byte)ALIGN_LEFT) != 0)
            {
                // Testo allineato a destra
                if (row.size() < 24)
                {
                    List<Byte> spaces = _getSpaces((24 - row.size()));
                    parsedTicket.addAll(spaces);
                    parsedTicket.addAll(row);
                }
                else
                {
                    parsedTicket.addAll(row);
                }
            }
            else
            {
                // Testo allineato a sinistra
                parsedTicket.addAll(row);
            }
            parsedTicket.add((byte) '\n');
        }


        byte[] bbticket=new byte[parsedTicket.size()];

        String str="";
        for (int i=0;i<parsedTicket.size();i++)
            bbticket[i]=parsedTicket.get(i);

        String h= null;
        try {
            h = new String(bbticket, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return h;
    }

    public static List<Byte> Ticket4Printer(byte[] ticket)
    {
        // Lo scontrino che dovrò stampare
        List<Byte> parsedTicket = new ArrayList<Byte>();

        // L'insieme di righe che compongono lo scontrino (i primi 4 bytes sono di formattazione)
        List<List<Byte>> splittedTicket = _splitTicket(ticket);

        for (List<Byte> row : splittedTicket)
        {
            // Dati di formattazione
            byte format = (byte) (row.get(0) | (row.get(1) << 8) | (row.get(2) << 16) | (row.get(3) << 24));

            // Rimuovo i byte di formattazione
            row.remove(0);
            row.remove(0);
            row.remove(0);
            row.remove(0);

            if (row.contains( Byte.valueOf((byte) 0xA4) )) // 164 ---> ¤
            {
                int index = row.indexOf(Byte.valueOf((byte) 0xA4));
                row.set(index - 1, (byte)' ');
                row.set(index + 0, (byte)'E');
                row.set(index + 1, (byte)'u');
                row.set(index + 2, (byte)'r');
                row.set(index + 3, (byte)'o');
            }

            // Sostituisco "û" [dovrebbe essere -] con "-".
            if (row.contains(Byte.valueOf((byte) 150) )) // 150 ---> û
            {
                row.set(row.indexOf(Byte.valueOf((byte) 150)), (byte) 45); // 45 ---> -
            }

            if ((format & (byte)PROPERTY_CLIENT) != 0)
            {
                // Se la riga deve apparire solo nello scontrino, non la aggiungo
                continue;
            }
            if ((format & (byte)FONT_TITLE) != 0)
            {
                // Altezza doppia (24 char/row)
                parsedTicket.add(Byte.valueOf((byte)0x1b));
                parsedTicket.add(Byte.valueOf((byte)0x21));
                parsedTicket.add(Byte.valueOf((byte)(0x02 | 0x10)));

            }
            else if ((format & (byte)FONT_COLLAPSED) != 0)
            {
                // Larghezza dimezzata (48 char/row)
                parsedTicket.add(Byte.valueOf((byte)0x1b));
                parsedTicket.add(Byte.valueOf((byte)0x21));
                parsedTicket.add(Byte.valueOf((byte)0x01));
            }
            else
            {
                // Larghezza normale (24 char/row)
                parsedTicket.add(Byte.valueOf((byte)0x1b));
                parsedTicket.add(Byte.valueOf((byte)0x21));
                parsedTicket.add(Byte.valueOf((byte)0x02));
            }

            // Analizzo l'allineamento
            if ((format & (byte)ALIGN_CENTER) != 0)
            {
                // Testo centrato
                if (row.size() < 24)
                {
                    List<Byte> spaces = _getSpaces((24 - row.size()) / 2);
                    parsedTicket.addAll(spaces);
                    parsedTicket.addAll(row);
                }
                else
                {
                    parsedTicket.addAll(row);
                }
            }
            else if ((format & (byte)ALIGN_LEFT) != 0)
            {
                // Testo allineato a destra
                if (row.size() < 24)
                {
                    List<Byte> spaces = _getSpaces((24 - row.size()));
                    parsedTicket.addAll(spaces);
                    parsedTicket.addAll(row);
                }
                else
                {
                    parsedTicket.addAll(row);
                }
            }
            else
            {
                // Testo allineato a sinistra
                parsedTicket.addAll(row);
            }
            parsedTicket.add((byte) '\n');
        }

        parsedTicket.add(Byte.valueOf((byte)'\n'));
        parsedTicket.add(Byte.valueOf((byte)'\n'));
        parsedTicket.add(Byte.valueOf((byte)'\n'));

        return parsedTicket;
    }



    private static List<Byte> _getSpaces(int n)
    {
        List<Byte> spaces = new ArrayList<Byte>(n);
        for (int i = 0; i < n; i++)
        {
            spaces.add((byte)' ');
        }
        return spaces;
    }

    /// <summary>
/// Split the RAW Ticket into an array of rows.
/// </summary>
    private static List<List<Byte>> _splitTicket(byte[] ticket)
    {
        List<Byte> row = new ArrayList<Byte>();
        List<List<Byte>> splittedTicket = new ArrayList<List<Byte>>();
        for (Byte currentChar : ticket)
        {
            if (currentChar != '\n')
            {
                row.add(currentChar);
            }
            else
            {
                splittedTicket.add(row);
                row = new ArrayList<Byte>();
            }
        }

        // Rimuovo le prime due e l'ultima riga
        splittedTicket.remove(0);
        splittedTicket.remove(splittedTicket.size() - 1);

        return splittedTicket;
    }
}
