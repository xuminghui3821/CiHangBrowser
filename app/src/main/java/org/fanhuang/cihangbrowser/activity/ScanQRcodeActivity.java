package org.fanhuang.cihangbrowser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by xiaohuihui on 2018/11/19.
 */

public class ScanQRcodeActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private QRCodeView mQRCodeView;
    private Boolean FlashLight = false;//闪光灯状态默认为关闭

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        mQRCodeView = (ZXingView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {//扫描二维码成功
        mQRCodeView.closeFlashlight();
        if (TextUtils.isEmpty(result)) {
            CustomToast.toast("请扫描有效的网址");
        } else {
            if (Patterns.WEB_URL.matcher(result).matches()) {
                Intent intent = new Intent();
                intent.putExtra("url", result);
                setResult(RESULT_OK, intent);
                ScanQRcodeActivity.this.finish();
            } else {
                CustomToast.toast("请扫描有效的网址");
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {//打开照相机错误
        mQRCodeView.closeFlashlight();//关闭闪光灯
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQRCodeView.onDestroy();
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_light:
                ImageView imageView = findViewById(R.id.open_light);
                if (FlashLight == false) {
                    mQRCodeView.openFlashlight();
                    imageView.setImageResource(R.mipmap.close_flashlight);
                    FlashLight = true;
                }else {
                    mQRCodeView.closeFlashlight();
                    imageView.setImageResource(R.mipmap.open_flashlight);
                    FlashLight = false;
                }
                break;
        }
    }
}