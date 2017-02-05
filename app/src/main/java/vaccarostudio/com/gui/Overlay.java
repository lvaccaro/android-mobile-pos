package vaccarostudio.com.gui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import vaccarostudio.com.verifone.R;

/**
 * Created by lucavaccaro on 06/03/14.
 */


public class Overlay {


    static private OverlayDialog handler;

    public static void show(Context context, String mMessage) {

        if (handler==null && context==null)
            ;
        else if(handler==null && context!=null)
            handler=new OverlayDialog(context,mMessage);
        else if(handler!=null && context==null)
            hide();
        else if (handler!=null && context.equals(handler.context))
            handler.update(mMessage);
        else {
            hide();
            handler = new OverlayDialog(context, mMessage);
        }

    }
    public static void hide(){
        if (handler!=null){
            try {
                handler.hide();
                handler.dismiss();
                handler = null;
            }catch(Exception e){
                handler = null;
            }
        }
    }



    protected static class OverlayDialog extends Dialog {
        static public Context context;
        TextView mText;

        public OverlayDialog(Context context_, String mMessage) {
            super(context_, R.style.AppTheme);

            if (context_ == null)
                return;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            context = context_;
            View v = getLayoutInflater().inflate(R.layout.fragment_overlay, null);
            mText = (TextView) v.findViewById(R.id.overlay_txt);
            mText.setText(mMessage);

            ColorDrawable cd = new ColorDrawable(android.graphics.Color.argb(0xd0, 0xff, 0xff, 0xff));
            getWindow().setBackgroundDrawable(cd);

            setCancelable(false);
            setCanceledOnTouchOutside(false);
            setContentView(v);

            super.show();
        }

        public  void update(String mMessage){
            if (mText!=null)
                mText.setText(mMessage);
        }

        public void dismiss() {
            super.dismiss();
        }
    }
}