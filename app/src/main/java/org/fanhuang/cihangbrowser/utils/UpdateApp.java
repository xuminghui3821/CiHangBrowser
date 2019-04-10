package org.fanhuang.cihangbrowser.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.interfaces.NetWorkCallBack;
import org.fanhuang.cihangbrowser.network.Commdbase;
import org.fanhuang.cihangbrowser.network.Config;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by xiaohuihui on 2018/12/13.
 */


public class UpdateApp {
    private Activity mActivity;

    public UpdateApp(Activity activity) {
        this.mActivity = activity;
        boolean AutoUpdata = (boolean) SharedPreferencesUtils.get(activity, "autoupdata", false);//查看当前是否是WIFI环境，默认为false
        if (AutoUpdata) {
            Commdbase commdbase = new Commdbase(activity, new NetWorkCallBack() {
                @Override
                public void onSuccess(int action, String response) {
                    try {
                        JSONObject jsonobject = new JSONObject(response);
                        String OnlineVersion = jsonobject.getString("Version");//线上版本编号
                        String LoactionVersion = VersionUtils.getVersionName(mActivity);//本地版本编号
                        final String url = jsonobject.getString("url");//下载地址
                        if (OnlineVersion.compareTo(LoactionVersion) > 0) {
                            if (!url.isEmpty()) {
                                upadte(url);
                            }
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int action, String error) {
                }
            });
            commdbase.requestGetStringDataMap(Config.update, 1);
        }
    }

    private void upadte(String s) {
        RequestParams requestParams = new RequestParams(s);
        requestParams.setSaveFilePath(Config.FILE_PATH);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DEFAULT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity
                mActivity.startActivity(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //CustomToast.toast("下载失败");

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }
}
