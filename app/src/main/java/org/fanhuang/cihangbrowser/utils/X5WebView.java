package org.fanhuang.cihangbrowser.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.URLUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.NewDownloadActiviy;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.download.DownloadManager;
import org.fanhuang.cihangbrowser.entity.AdvertisingUrl;
import org.fanhuang.cihangbrowser.gen.AdvertisingUrlDao;
import org.fanhuang.cihangbrowser.network.Config;
import org.xutils.ex.DbException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.fanhuang.cihangbrowser.filter.UrlFilter.URLDecoderString;

/**
 * Created by xiaohuihui on 2018/11/8.
 */


public class X5WebView extends WebView {

    public int down_x = 0;
    public int down_y = 0;
    public MotionEvent b = null;
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }



        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
            if (s.contains("[tag]")) {
                String localPath = s.replaceFirst("^http.*[tag]\\]", "");
                try {
                    InputStream is = getContext().getAssets().open(localPath);
                    String mimeType = "text/javascript";
                    if (localPath.endsWith("css")) {
                        mimeType = "text/css";
                    }
                    return new WebResourceResponse(mimeType, "UTF-8", is);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }


    };

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(final String url, String userAgent, final String contentDisposition,
                                    String mimeType, long contentLength) {
            String FileName = URLUtil.guessFileName(url, contentDisposition, mimeType);//获取文件名
            try {
                FileName = URLDecoder.decode(FileName, "UTF-8");//解码url
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            final String a = (String) SharedPreferencesUtils.get(getContext(), "download", Config.FILE_PATH);//安装包地址
            final Dialog dialog = new AlertDialog.Builder(getContext()).create();
            final View views = LayoutInflater.from(getContext()).inflate(R.layout.view_download_home, null);
            dialog.show();
            dialog.getWindow().setContentView(views);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            TextView textView = (TextView) views.findViewById(R.id.fileSiz);
            if (contentLength != -1)
                textView.setText(String.valueOf("文件大小：" + Utlis.convertFileSize(contentLength)));
            else
                textView.setText(String.valueOf("文件大小：未知"));
            final EditText fileName = (EditText) views.findViewById(R.id.fileName);
            fileName.setText(FileName);
            if (!TextUtils.isEmpty(fileName.getText())) {
                Selection.setSelection(fileName.getText(), fileName.getText().length() - 4);
                fileName.selectAll();
            }
            TextView cancel = (TextView) views.findViewById(R.id.cancel);
            cancel.setOnClickListener(new OnClickListener() {//取消按钮
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            TextView query = (TextView) views.findViewById(R.id.query);
            query.setOnClickListener(new OnClickListener() {//确定按钮
                @Override
                public void onClick(View v) {
                    String newLable = fileName.getText().toString().trim();
                    if (TextUtils.isEmpty(newLable)) {
                        CustomToast.toast("文件名不能为空");
                    } else {
                        try {
                            DownloadManager.getInstance().startDownload(
                                    url, newLable,
                                    a + newLable, true, true, null);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        getContext().startActivity(new Intent(getContext(), NewDownloadActiviy.class));
                        dialog.dismiss();
                    }
                }
            });
        }
    };


    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {//获取点击事件
        int action = paramMotionEvent.getAction();
//        int i1 = (int) paramMotionEvent.getX();
//        int i3 = (int) paramMotionEvent.getY();
        if (action == ACTION_DOWN) {
//            if ((this.z != null) && (this.z.getVisibility() == 0)) {
//                this.z.getHitRect(this.x);
//            }
//            this.k = this.x.contains(i1, i3);
            this.down_x = ((int) paramMotionEvent.getX());
            this.down_y = ((int) paramMotionEvent.getY());
           //getHandler().removeCallbacks(this.n);
            this.b = MotionEvent.obtain(paramMotionEvent);
//            this.s = null;
        }
        return super.dispatchTouchEvent(paramMotionEvent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        this.setWebViewClient(client);
        this.setDownloadListener(downloadListener);
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {//设置WebView的基本参数
        WebSettings webSetting = this.getSettings();
        webSetting.setAllowFileAccess(true);  //设置支持文件流
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);   //调整到适合webview大小
        webSetting.setSupportZoom(true);   //支持缩放
        webSetting.setBuiltInZoomControls(true);   //原网页基础上缩放
        webSetting.setUseWideViewPort(true);   //调整到适合webview大小
        webSetting.setSupportMultipleWindows(false);
        webSetting.setAppCacheEnabled(true);   //开启缓存机制
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);   //开启dom
        webSetting.setJavaScriptEnabled(true);  //开启javascript
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCachePath(Config.FILE_PATH);
        webSetting.setDisplayZoomControls(false);
        //webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);//使用缓存
        //
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//不用缓存
        webSetting.setSaveFormData(true);
        webSetting.setSavePassword(true);
        webSetting.setDefaultTextEncodingName("UTF-8");   //设置字符编码
        webSetting.setAppCachePath(getContext().getDir("appcache", 0).getPath());
        //提高网页加载速度,暂时阻塞图片加载,等网页加载好了,再进行加载图片
        webSetting.setBlockNetworkImage((boolean) SharedPreferencesUtils.get(getContext(), "no_img", false));//时间而终是否是无图模式
        setWebSize((int) SharedPreferencesUtils.get(getContext(), "typeface", 3), webSetting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        webSetting.setAllowUniversalAccessFromFileURLs(true);

    }

    public X5WebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

    private void setWebSize(int mUrlStartNum, WebSettings settings) {//设置文本大小
        switch (mUrlStartNum) {
            case 1:
                settings.setTextSize(WebSettings.TextSize.SMALLEST);
                break;
            case 2:
                settings.setTextSize(WebSettings.TextSize.SMALLER);
                break;
            case 3:
                settings.setTextSize(WebSettings.TextSize.NORMAL);
                break;
            case 4:
                settings.setTextSize(WebSettings.TextSize.LARGER);
                break;
            case 5:
                settings.setTextSize(WebSettings.TextSize.LARGEST);
                break;
        }
    }

}
