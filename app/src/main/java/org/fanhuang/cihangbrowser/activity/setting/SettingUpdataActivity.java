package org.fanhuang.cihangbrowser.activity.setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.interfaces.NetWorkCallBack;
import org.fanhuang.cihangbrowser.network.Commdbase;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.VersionUtils;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/14.
 */


public class SettingUpdataActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.versionCode)
    TextView versionCode;
    @BindView(R.id.wifiDoweanIcon)
    ImageView wifiDoweanIcon;

    private boolean AutoUpdata;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_me);
        ButterKnife.bind(this);
        title.setText("版本介绍");
        init();

    }

    private void init() {
        AutoUpdata = (boolean) SharedPreferencesUtils.get(this, "autoupdata", false);
        if (AutoUpdata) {
            wifiDoweanIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            wifiDoweanIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        versionCode.setText(String.valueOf("慈航浏览器" + VersionUtils.getVersionName(this)));
    }

    @OnClick(R.id.back)
    public void back() {
        this.finish();
    }

    @OnClick(R.id.wifiDowean)
    public void wifiDowean() {
        SharedPreferencesUtils.put(this, "autoupdata", !AutoUpdata);
        AutoUpdata = (boolean) SharedPreferencesUtils.get(this, "autoupdata", true);
        if (AutoUpdata) {
            wifiDoweanIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            wifiDoweanIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
    }

    @OnClick(R.id.update)
    public void update(View view) {
        requestUpdate();
    }

    private void requestUpdate() {
        Commdbase commdbase = new Commdbase(getApplicationContext(), new NetWorkCallBack() {
            @Override
            public void onSuccess(int action, String response) {
                try {
                    JSONObject jsonobject = new JSONObject(response);
                    String OnlineVersion = jsonobject.getString("Version");//线上版本编号
                    String LoactionVersion = VersionUtils.getVersionName(getApplicationContext());//本地版本编号
                    String Date= jsonobject.getString("Date");//版本日期
                    final String url = jsonobject.getString("url");//下载地址
                    if(OnlineVersion.compareTo(LoactionVersion) > 0) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingUpdataActivity.this);
                        String Title = "发现新版本 " + OnlineVersion;
                        builder.setTitle(Title);
                        builder.setMessage("是否更新到最新版本?");
                        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!url.isEmpty()) {
                                    myDownload(url);
                                }
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else {
                        CustomToast.toast("暂无新版本");
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int action, String error) {
                Log.e("xiaohuihui",error);
            }
        });
        commdbase.requestGetStringDataMap(Config.update, 1);
    }

    private void myDownload(String url) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("亲，努力下载中。。。");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"jjapp/");
        params.setAutoRename(true);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                progressDialog.show();
            }

            @Override
            public void onLoading(long l, long l1, boolean b) {
                progressDialog.setProgress((int)(l1*100/l));
            }

            @Override
            public void onSuccess(File file) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                SettingUpdataActivity.this.startActivity(intent);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressDialog.dismiss();
                Toast.makeText(SettingUpdataActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}

