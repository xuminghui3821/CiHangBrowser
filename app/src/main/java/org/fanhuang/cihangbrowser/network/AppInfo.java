package org.fanhuang.cihangbrowser.network;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class AppInfo {
    public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public Drawable appIcon=null;
    public void print(){
        Log.v("app","Name:"+appName+",Package:"+packageName+",versionName:"+versionName+",versionCode:"+versionCode);
    }
}
