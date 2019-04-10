package org.fanhuang.cihangbrowser.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import org.fanhuang.cihangbrowser.app.MyAppAction;

/**
 * Created by xiaohuihui on 2018/11/13.
 */

public class CustomToast {

    /**
     * 保证在UI线程中显示Toast
     */
    private static Toast mToast = null;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (mToast != null) {
                mToast.cancel();
            }
            String text = (String) msg.obj;
            mToast = Toast.makeText(MyAppAction._Contenxt, text, Toast.LENGTH_SHORT);
            mToast.show();
        }
    };

    public static void toast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    private static void showToast(String text, int duration) {
        mHandler.sendMessage(mHandler.obtainMessage(0, 0, duration, text));
    }
}