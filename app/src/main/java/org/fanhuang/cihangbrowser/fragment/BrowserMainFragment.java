package org.fanhuang.cihangbrowser.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebIconDatabase;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.BrowserMainActivity;
import org.fanhuang.cihangbrowser.activity.ScanQRcodeActivity;
import org.fanhuang.cihangbrowser.activity.SearchActivity;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.entity.AdvertisingUrl;
import org.fanhuang.cihangbrowser.entity.BookmarkEntity;
import org.fanhuang.cihangbrowser.entity.History;
import org.fanhuang.cihangbrowser.entity.NoImageWhiteList;
import org.fanhuang.cihangbrowser.entity.UserBlackUrl;
import org.fanhuang.cihangbrowser.entity.UserWhiteUrl;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.AdvertisingUrlDao;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.gen.HistoryDao;
import org.fanhuang.cihangbrowser.gen.NoImageWhiteListDao;
import org.fanhuang.cihangbrowser.gen.UserBlackUrlDao;
import org.fanhuang.cihangbrowser.gen.UserWhiteUrlDao;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.Utlis;
import org.fanhuang.cihangbrowser.utils.X5WebView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jameson.io.library.util.LogUtils;
import me.yokeyword.fragmentation.SupportFragment;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.fanhuang.cihangbrowser.filter.UrlFilter.ISChinaIP;
import static org.xutils.common.util.FileUtil.getCacheDir;

/**
 * Created by xiaohuihui on 2018/11/6.
 */

public class BrowserMainFragment extends SupportFragment implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.header_web_icon)
    ImageView web1Icon;
    @BindView(R.id.header_web_title)
    TextView web1Title;
    @BindView(R.id.text_context)
    LinearLayout textContext;
    @BindView(R.id.news_view_header_layout)
    LinearLayout newsViewHeaderLayout;
    @BindView(R.id.refresh)
    ImageView refresh;
    @BindView(R.id.progressBar1)
    ProgressBar mPageLoadingProgressBar;
    @BindView(R.id.webView1)
    FrameLayout mViewParent;
    @BindView(R.id.QRCode)
    ImageView QRCode;
    @BindView(R.id.exit_text)
    TextView exittext;
    @BindView(R.id.menu)
    ImageView menu;

    private String strUrl = "";
    private View view;
    private BookmarkEntityDao bookmarkEntityDao;//用于编辑书签数据库
    private UserBlackUrlDao userblackUrlDao;//用于添加用户添加的网站黑名单
    private UserWhiteUrlDao userwhiteUrlDao;//用于添加用户添加的网站白名单
    private NoImageWhiteListDao noImageWhiteListDao;//用于添加用户添加的网站无图白名单
    private HistoryDao historyDao;//用于编辑历史信息数据库
    private AdvertisingUrlDao mAdvertisingUrlDao;//用于广告链接的查询
    private BrowserMainActivity parentActivity;
    public X5WebView mWebView;
    private boolean ISCOMPLETE;//是否完成
    public boolean isShowWeb = false;//当前是否显示页面
    private OnItemOclickListers onItemOclickListers;//存放的由Activity返回的回调接口，方便fragment和Activity之间传递数据
    private boolean ISEXIT = false;//
    public MyHangler myHangler = new MyHangler();
    private LocationClient mLocationClient;
    private Bitmap sitesLogo = null;//网站LOGO
    private static Handler handler = new Handler();
    private int downX, downY;
    public Map<String, String> FilterMap = new HashMap<String, String>();
    private Boolean Is404 = false;

    public static final int NORMAL = 0;//默认方式
    public static final int MIDDLE = 1;// 中间态
    public static final int NOFILTER = 2;//不过滤状态
    private int NoFilter = NORMAL;//当前刷新不过滤
    private int ImageNoFilter = NORMAL;//当前图片不过滤

    public static BrowserMainFragment newInstance() {
        return new BrowserMainFragment();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {//如果安卓版本超过11
                getActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,//硬件加速
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        parentActivity = ((BrowserMainActivity) getActivity());//获取绑定该Fragment的activity

    }

    public static String getDirs(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        WebIconDatabase.getInstance().open(getDirs(getCacheDir("icons/").getAbsolutePath()));//获取网站的favicon.ico图标,必须设置的操作
        WebViewinit();
        initFirstShow();
        initViews();
        initProgressBar();
        return view;
    }

    //首先加载一个页面
    private void initFirstShow() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        sitesLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.default_logo, options);
        strUrl = String.valueOf(SharedPreferencesUtils.get(parentActivity, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html"));
        if (parentActivity.externalurl != "") {
            strUrl = parentActivity.externalurl;//从外部吊起的浏览器所传递的参数
            parentActivity.externalurl = "";
        }
        //strUrl = "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html";
        Config.isShowHome = true;
        QRCode.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        //newsViewHeaderLayout.setVisibility(View.VISIBLE);
        showWeb();
    }

    private void initProgressBar() {//初始化进度条按钮
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }

    public void initViews() {
        bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
        userblackUrlDao = MyAppAction.getInstances().getDaoSession().getUserBlackUrlDao();
        userwhiteUrlDao = MyAppAction.getInstances().getDaoSession().getUserWhiteUrlDao();
        noImageWhiteListDao = MyAppAction.getInstances().getDaoSession().getNoImageWhiteListDao();
        historyDao = MyAppAction.getInstances().getDaoSession().getHistoryDao();
        mAdvertisingUrlDao = MyAppAction.getInstances().getDaoSession().getAdvertisingUrlDao();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//未获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {//获取定位权限
            }//表明用户没有彻底禁止该权限
            //申请权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x01);
        } else {//已经有定位权限了则初始化百度定位功能
            dinwei();
        }
    }

    public void dinwei() {//初始化定位功能
        // 声明LocationClient类
        mLocationClient = new LocationClient(getActivity());
        BDLocationListener mBDLocationListener = new MyBDLocationListener();
        // 注册监听
        mLocationClient.registerLocationListener(mBDLocationListener);
        // 声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02
        option.setScanSpan(5000);// 设置发起定位请求的时间间隔 单位ms
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向
        // 设置定位参数
        mLocationClient.setLocOption(option);
        // 启动定位
        mLocationClient.start();
    }


    private class MyBDLocationListener implements BDLocationListener {//百度定位

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                if (mLocationClient.isStarted()) {
                    // 获得位置之后停止定位
                    mLocationClient.stop();
                }
            }
        }
    }


    @OnClick(R.id.QRCode)
    public void QRCode(View view) {//获取摄像头权限准备扫描二维码
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//查看是否有摄像头权限如果没有摄像头权限则申请
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA}, 6);
        } else {//打开扫描二维码
            getActivity().startActivityForResult(new Intent(getActivity(), ScanQRcodeActivity.class), 0x456);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    /**
     * @param requestCode  标识
     * @param grantResults 用户获取的权限信息
     */
    private void doNext(final int requestCode, final int[] grantResults) {
        try {
            if (requestCode == 6) {//摄像头权限申请成功
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("xiaohuihui", "打开摄像头权限申请成功");
                    getActivity().startActivityForResult(new Intent(getActivity(), ScanQRcodeActivity.class), 0x456);
                } else {
                    CustomToast.toast("请赋予权限");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {//记录按下的坐标
        downX = (int) event.getX();
        downY = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {//滑动事件
            QRCode.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
        }
        return false;
    }

    private void showWeb() {
        try {
            isShowWeb = true;
            LoadUrl(strUrl);
            if (Config.isShowHome) {
                QRCode.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            } else {
                QRCode.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }


    public void LoadUrl(String Url) {
        Log.e("xiaohuihui", Url);
        if (Url.equals("404")) {
            Is404 = true;
            mWebView.loadUrl("file:///android_asset/index.html");
        } else {
            Is404 = false;
            mWebView.loadUrl(Url);
        }

    }

    public void Filter(String url) {//用于过滤网站
        if (!url.equals("file:///android_asset/index.html"))
            Is404 = false;
        if (url.contains("android_asset"))//包含这个字符串则不进行过滤
            return;
        final String mUrl = url;
        new Thread() {//由于读取数据文件会浪费大量的时间所以放到线程中进行处理
            public void run() {
                if (Config.FilterOutsideIPFlag && !ISChinaIP(mUrl)) {//如果要访问的网站不在中国境内则将页面设置为404
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadUrl("404");
                            return;
                        }
                    });
                }
                if (Config.FilterMode && UrlFilter.FilterUrlList(mUrl)) {//黑白名单过滤
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadUrl("404");
                            return;
                        }
                    });
                }
                if (Config.KeyWordFilter && UrlFilter.KeyWordFilterToUrl(mUrl)) {//查看链接中的关节自
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadUrl("404");
                            return;
                        }
                    });
                }
            }
        }.start();
    }

    public String getFromAssets(String fileName) {// 用于获取assets中的资源文本
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    private void deleteimg() {//删除图片
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.loadUrl("javascript:function hideImg(adv) {\n" +
                            "    var imgs = document.getElementsByTagName(\"img\");\n" +
                            "    for (var i = 0; i < imgs.length; i++) {\n" +
                            "        imgs[i].remove();\n" +
                            "    }\n" +
                            "}");
                    mWebView.loadUrl("javascript:hideImg();");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }

    private void hideHtmlContent() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.loadUrl("javascript:function hideOther() {\n" +
                            "    var Link = document.getElementsByTagName(\"A\");\n" +
                            "    for (var i = 0; i < Link.length; i++) {\n" +
                            "        if (mbrowser.IsKeyWord(Link[i].innerHTML)) {\n" +
                            "            Link[i].remove();\n" +
                            "            continue;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}");
                    mWebView.loadUrl("javascript:hideOther();");
//                    if (Config.KeyWordFilter) {
//                        if (Config.GuanggaoFilter)
//                            mWebView.loadUrl("javascript:hideOther(1,0);");
//                        else
//                            mWebView.loadUrl("javascript:hideOther(1,1);");
//                    } else if (Config.GuanggaoFilter)
//                        mWebView.loadUrl("javascript:hideOther(0,1);");

                    //获取网站源码
//                    mWebView.loadUrl("javascript:function showSource(){\n" +
//                            "    mbrowser.showSource(document.getElementsByTagName(\"html\")[0].innerHTML);\n" +
//                            "}");
//                    mWebView.loadUrl("javascript:showSource();");


                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }

    private void select_text() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LoadJsfile();
                    int height = mWebView.getHeight();
                    int Width = mWebView.getWidth();
                    mWebView.loadUrl("javascript:select_text(" + mWebView.down_x + "," + mWebView.down_y + "," + height + "," + Width + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class AndroidtoJs extends Object {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public boolean IsKeyWord(String msg) {//判断是否存在关键字 被JS调用
            //return true;
            String reg = "[^\u4e00-\u9fa5]";
            msg = msg.replaceAll(reg, "");
            String flag = FilterMap.get(msg);
            if (flag != null) {
                if (flag.equals("1"))
                    return true;
                else
                    return false;
            }

            if (Config.KeyWordFilter && UrlFilter.KeyWordFilterToUrl(msg)) {
                FilterMap.put(msg, "1");
                return true;
            } else {
                FilterMap.put(msg, "0");
                return false;
            }
        }

        @JavascriptInterface
        public boolean showSource(String html) {
            String str = html;
            return true;
        }

        @JavascriptInterface
        public boolean AdvertisingFilter(String url) {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            List<AdvertisingUrl> entity = mAdvertisingUrlDao.queryBuilder().where(AdvertisingUrlDao.Properties.Url.eq(host)).list();// 查找是否存在相同数据
            if (entity.size() > 0)
                return true;
            else
                return false;
        }


        @JavascriptInterface
        public void log(String paramString) {
            Log.i("jslog", paramString);
        }

        @JavascriptInterface
        public void sendLongPress(int x, int y) {
            final int _x = x;
            final int _y = y;
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        long l = SystemClock.uptimeMillis();
                        MotionEvent localMotionEvent1 = MotionEvent.obtain(l, l, 0, _x, _y, 0);
                        mWebView.dispatchTouchEvent(localMotionEvent1);//通过逆向X浏览器获得的代码，其实后续还有很多操作，但是经过实际操作发现运行到这里就可以了
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public static String getJsFile(InputStream paramInputStream) {
        ByteArrayOutputStream localByteArrayOutputStream;
        try {
            localByteArrayOutputStream = new ByteArrayOutputStream();
            byte[] arrayOfByte = new byte[1024];
            for (; ; ) {
                int i = paramInputStream.read(arrayOfByte);
                if (i == -1) {
                    break;
                }
                localByteArrayOutputStream.write(arrayOfByte, 0, i);
            }
            localByteArrayOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            paramInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        return localByteArrayOutputStream.toString();
    }

    private void LoadJsfile() {//加载js文件
        try {
            String paramString = getJsFile(mWebView.getContext().getAssets().open("js/" + "inject_nightmode.js"));
            mWebView.loadUrl("javascript:" + paramString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void WebViewinit() {
        mWebView = new X5WebView(parentActivity, null);
        mWebView.addJavascriptInterface(new AndroidtoJs(), "mbrowser");
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String s) {
                super.onLoadResource(webView, s);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {//拦截


                if (Config.GuanggaoFilter == true && MyAppAction.getInstances().adblockPlus.InitOver &&
                        MyAppAction.getInstances().adblockPlus.UrlFilter(s))//开启了广告过滤功能 并且初始化完毕,并且该链接被屏蔽
                {
                    Config.ADVFilterNum++;
                    WebResourceResponse response = null;
                    response = new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
                    return response;
                }
                if (NoFilter != NORMAL)//当前不过滤
                    return super.shouldInterceptRequest(webView, s);
                Uri uri = Uri.parse(s);
                String url = uri.toString();
                if (Config.KeyWordFilter && !UrlFilter.UrlIsNoFilter(url)) {//
                    if (UrlFilter.KeyWordFilterToUrl(url)) {//链接域名检测如果包含关键字则过滤
                        WebResourceResponse response = null;
                        response = new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
                        return response;
                    }
                }

                String filetype = url.substring(url.length() - 5, url.length());
                if (!(filetype.contains(".png") || filetype.contains(".jpg") ||
                        filetype.contains(".ico") || filetype.contains(".gif") ||
                        filetype.contains(".bmp") || filetype.contains(".js"))) {
                    if (Config.KeyWordFilter == true) {//关键字过滤模式
                        hideHtmlContent();
                    }
                    if (ImageNoFilter == NORMAL && Config.NoImage == true) {//无图模式
                        deleteimg();
                    }
                }
                return super.shouldInterceptRequest(webView, s);
            }


//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {//拦截
//
//                if (NoFilter != NORMAL)//当前不过滤
//                    return super.shouldInterceptRequest(webView, webResourceRequest);
//
//                Uri uri = webResourceRequest.getUrl();
//                String url = uri.toString();
//                if (Config.KeyWordFilter && !UrlFilter.UrlIsNoFilter(url)) {//网址拦截
//                    if (UrlFilter.KeyWordFilterToUrl(url)) {//链接域名检测如果包含关键字则过滤
//                        WebResourceResponse response = null;
//                        response = new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
//                        return response;
//                    }
//                }
//                Map<String, String> Headers = webResourceRequest.getRequestHeaders();
//                String accept = Headers.get("Accept");
//                if (Config.ContentFilter == true) {//如果是强力过滤则使用该方法
//                   // if (accept.contains("html") || accept.contains("css") || accept.contains("*/*"))
//                        hideHtmlContent();
//                }
//                if (ImageNoFilter == NORMAL && Config.NoImage == true)//无图模式
//                {
////                    String filetype = url.substring(url.length() - 5, url.length());
////                    if (filetype.contains(".png") || filetype.contains(".jpg") || filetype.contains(".ico") || filetype.contains(".gif") || filetype.contains(".bmp")) {
////                        WebResourceResponse response = null;
////                        response = new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
////                        return response;
////                    }
//                    deleteimg();
//                }
//                return super.shouldInterceptRequest(webView, webResourceRequest);
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                strUrl = s;//记录点击的链接
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {//页面加载结束
                try {
                    if (NoFilter == NORMAL) {
                        if (Config.KeyWordFilter == true)
                            hideHtmlContent();
                        if (ImageNoFilter == NORMAL && Config.NoImage == true)//无图模式
                            deleteimg();
                    }
                    SharedPreferencesUtils.put(getActivity(), "filternum", Config.FilterNum);//更新过滤个数
                    SharedPreferencesUtils.put(getActivity(), "guanggaofilternum", Config.ADVFilterNum);//更新广告过滤个数
                    super.onPageFinished(view, url);
                    mPageLoadingProgressBar.setVisibility(View.GONE);//加载结束 将进度条隐藏
                    ISCOMPLETE = true;
                    refresh.setImageResource(R.mipmap.small_refresh);//页面加载结束之后更新图标
                    strUrl = url;
                    //作用:判断webview能否前进,后退,与下方if语句重复
                    addHistory(view);//添加历史
                    //根据webview是否可"返回","前进"设置背景图片
                    if (view.canGoBack()) {
                        //webview可以返回
                        parentActivity.setImageSrc(0);
                    } else {
                        //webview不可返回
                        parentActivity.setImageSrc(1);
                    }
                    if (view.canGoForward()) {
                        //webview可以前进
                        parentActivity.setImageSrc(2);
                    } else {
                        //webview不可前进
                        parentActivity.setImageSrc(3);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {//打开网站之前
                try {
                    if (s.indexOf("fhzd.org") != -1 || s.indexOf("veg001") != -1)//这两个域名不过滤
                    {
                        NoFilter = MIDDLE;//当前刷新不过滤
                        ImageNoFilter = MIDDLE;//当前图片不过滤
                        mWebView.getSettings().setBlockNetworkImage(false);//关闭无图模式
                        return;
                    }
                    FilterMap.clear();//清空过滤词汇栈
                    if (NoFilter == NOFILTER)//不进行过滤
                        NoFilter = MIDDLE;
                    else if (NoFilter == MIDDLE)
                        NoFilter = NORMAL;

                    if (ImageNoFilter == NOFILTER) {//当前不过滤图片
                        ImageNoFilter = MIDDLE;
                        if (Config.NoImage == true)
                            mWebView.getSettings().setBlockNetworkImage(false);
                    } else if (ImageNoFilter == MIDDLE) {
                        ImageNoFilter = NORMAL;
                        mWebView.getSettings().setBlockNetworkImage(Config.NoImage);
                    }
                    strUrl = s;
                    //查询库看一下当前的链接是否是无图白名单
                    Uri uri = Uri.parse(strUrl);
                    String host = uri.getHost();
                    List<NoImageWhiteList> entitylist = noImageWhiteListDao.queryBuilder().where(NoImageWhiteListDao.Properties.Url.like("%" + host + "%")).list();
                    if (entitylist.size() > 0) {
                        for (int i = 0; i < entitylist.size(); i++) {
                            if (strUrl.contains(entitylist.get(i).getUrl())) {
                                ImageNoFilter = MIDDLE;
                                if (Config.NoImage == true)
                                    mWebView.getSettings().setBlockNetworkImage(false);//关闭无图模式
                                break;
                            }
                        }
                    }

                    //查看当前链接是否是网站白名单
                    UserWhiteUrl userWhiteUrl = userwhiteUrlDao.queryBuilder().where(UserWhiteUrlDao.Properties.Url.eq(host)).unique();
                    if (userWhiteUrl != null) {//不过滤当前页面
                        NoFilter = MIDDLE;
                    }
                    Filter(s);
                    refresh.setImageResource(R.mipmap.small_refresh);
                    web1Icon.setImageResource(R.mipmap.default_logo);//打开新页面之前修改域名LOGO
                    ISCOMPLETE = false;
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler
                    sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();  // 接受所有网站的证书
                webView.getSettings().setJavaScriptEnabled(true);
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }
        });


        mWebView.setPictureListener(new WebView.PictureListener() {//加载事件及完成更新
            @Override
            public void onNewPicture(WebView webView, Picture picture) {
            }
        });

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    WebView.HitTestResult result = mWebView.getHitTestResult();
                    if (null == result)
                        return false;
                    int type = result.getType();
                    final String url = result.getExtra();
                    if (url == null || !isHttpUrl(url))
                        return false;
                    else {
                        final PopupWindow mPopWindow;
                        View contentView = LayoutInflater.from(parentActivity).inflate(R.layout.popuplayout, null);
                        mPopWindow = new PopupWindow(contentView,
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                        mPopWindow.setContentView(contentView);
                        //设置各个控件的点击响应
                        Button tv1 = (Button) contentView.findViewById(R.id.add_adv);
                        tv1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse(url);
                                String host = uri.getHost();
                                AdvertisingUrlDao advertisingUrlDao = MyAppAction.getInstances().getDaoSession().getAdvertisingUrlDao();//用于广告链接的查询
                                List<AdvertisingUrl> entity = advertisingUrlDao.queryBuilder().where(AdvertisingUrlDao.Properties.Url.eq(host)).list();// 查找是否存在相同数据
                                if (entity.size() == 0) {
                                    AdvertisingUrl advertisingUrl = new AdvertisingUrl();
                                    advertisingUrl.setUrl(host);
                                    advertisingUrlDao.insert(advertisingUrl);//加入到广告列表中
                                }
                                CustomToast.toast("添加成功");
                                mPopWindow.dismiss();
                            }
                        });

                        Button tv2 = (Button) contentView.findViewById(R.id.new_title);//在新窗口打开
                        tv2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemOclickListers.AddFragment(url);
                                mPopWindow.dismiss();
                            }
                        });

                        Button tv3 = (Button) contentView.findViewById(R.id.freecopy);//在新窗口打开
                        tv3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                select_text();
                                mPopWindow.dismiss();
                            }
                        });

                        View rootview = LayoutInflater.from(parentActivity).inflate(R.layout.activity_browser_main, null).findViewById(R.id.llayoutviewpage);
                        mPopWindow.setBackgroundDrawable(new ColorDrawable());//点击外部区域可以关闭
                        int resourceId = parentActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
                        int stateheight = parentActivity.getResources().getDimensionPixelSize(resourceId);
                        mPopWindow.setTouchable(true);
                        mPopWindow.showAtLocation(rootview, Gravity.TOP | Gravity.START, max(0, mWebView.down_x - 120), stateheight + mWebView.down_y + 140);
                        return true;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    return false;
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {//获取到标题的消息
                super.onReceivedTitle(webView, s);

                if (Config.KeyWordFilter && UrlFilter.KeyWordFilterToStr(s)) {//
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LoadUrl("404");
                            return;
                        }
                    });
                }
                setStrUrl(webView.getUrl());
                web1Title.setText(s);
            }

            @Override
            public void onReceivedIcon(WebView webView, Bitmap bitmap) {//修改ICON
                super.onReceivedIcon(webView, bitmap);
                web1Icon.setImageBitmap(bitmap);
                sitesLogo = bitmap;
                UpDataLogo(webView.getUrl(), bitmap);
            }

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {//进度条改变
                super.onProgressChanged(webView, newProgress);
                if (newProgress == 100) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                } else {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                    mPageLoadingProgressBar.setProgress(newProgress);
                }
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
                WebView a = (WebView) view;
                FrameLayout normalView = (FrameLayout) getActivity().findViewById(R.id.web_filechooser);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        mWebView.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onAttach(Context context) {//现在 Android 开发多使用一个 Activity 管理多个 Fragment 进行开发，不免需要两者相互传递数据，一般是给 Fragment 添加回调接口，让 Activity 继承并实现。回调接口一般都写在 Fragment 的onAttach()方法中
        super.onAttach(context);
        if (context instanceof OnItemOclickListers) {
            onItemOclickListers = (OnItemOclickListers) context;
        }
    }

    public interface OnItemOclickListers {
        void AddFragment(String url);
    }

    private void hidWeb() {
        try {
            isShowWeb = false;
            mViewParent.setVisibility(View.VISIBLE);        //webview
            strUrl = String.valueOf(SharedPreferencesUtils.get(parentActivity, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html"));
            Config.isShowHome = true;
            showWeb();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    //返回 防止空页面
    public void goBackInWebView(int index) {//后退到上一个页面
        try {
            WebBackForwardList history = mWebView.copyBackForwardList();
            String url = null;
            String title = null;
            while (mWebView.canGoBackOrForward(index)) {
                if (!history.getItemAtIndex(history.getCurrentIndex() + index).getUrl().equals("about:blank")) {
                    mWebView.goBackOrForward(index);//
                    url = history.getItemAtIndex(-index).getUrl();
                    history.getItemAtIndex(-index).getTitle();
                    web1Title.setText(title);
                    Log.e("tag", "first non empty" + url);
                    break;
                }
                index--;
            }
            if (url == null) {
                hidWeb();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public String getStrUrl() {
        return strUrl;
    }

    public String getTilte() {
        try {
            if (mWebView != null) {
                if (TextUtils.isEmpty(mWebView.getTitle())) {
                    return "首页";
                } else {
                    if (isShowWeb) {
                        return mWebView.getTitle();
                    } else {
                        return "首页";
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            return "首页";
        }
    }

    public void lookUp(String look) {
        try {
            if (mWebView != null) {
                int all = mWebView.findAll(look);
                if (all == 0) {
                    Toast.makeText(parentActivity, "查找成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(parentActivity, "查找无结果", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(parentActivity, "查找无结果", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public String getAddress() {
        try {
            if (mWebView != null) {
                if (TextUtils.isEmpty(mWebView.getUrl())) {
                    return "about:blank";
                } else {
                    if (isShowWeb) {
                        return mWebView.getUrl();
                    } else {
                        return "about:blank";
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            return "about:blank";
        }

    }

    public Bitmap GetsitesLogo() {
        return sitesLogo;
    }

    private void addBookMark() {
        try {
            if (isShowWeb) {
                boolean isOK = true;
                BookmarkEntity entity = new BookmarkEntity();
                entity.setTime(Utlis.getTime());
                entity.setTitle(mWebView.getTitle());
                entity.setUrl(mWebView.getUrl());
                if (mWebView.getFavicon() != null) {
                    entity.setIco(Utlis.GetBitmapByte(mWebView.getFavicon()));
                } else {
                    BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.suggestion_history);
                    entity.setIco(Utlis.GetBitmapByte(bd.getBitmap()));
                }
                List<BookmarkEntity> histories = bookmarkEntityDao.queryBuilder().orderAsc(BookmarkEntityDao.Properties.Id).list();
                for (int i = 0; i < histories.size(); i++) {
                    if (histories.get(i).getUrl().equals(mWebView.getUrl())) {
                        entity.setId(histories.get(i).getId());
                        isOK = false;
                    }
                }
                if (isOK) {
                    bookmarkEntityDao.insert(entity);
                    CustomToast.toast("书签添加成功");
                } else {
//                bookmarkEntityDao.update(entity);
                    CustomToast.toast("请勿重复添加!");
                }
            } else {
                CustomToast.toast("书签添加失败");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void UpDataLogo(String Url, Bitmap bitmap) {//网站LOGO加载完成之后会更新列表中的LOGO
        try {
            List<History> histories = historyDao.queryBuilder().orderDesc(HistoryDao.Properties.Id).list();//倒叙查询
            for (int i = 0, j = 0; i < histories.size() && j < 5; i++, j++) {
                String OldUrl = Utlis.GetHost(histories.get(i).getUrl());//获取链接的域名
                String NewUrl = Utlis.GetHost(Url);//获取链接的域名
                if (OldUrl != null && NewUrl != null && OldUrl.equals(NewUrl)) {
                    History history = histories.get(i);
                    history.setIco(Utlis.GetBitmapByte(bitmap));
                    historyDao.update(history);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void addHistory(WebView webView) {
        try {
            History history = new History();
            history.setTime(Utlis.getTime());
            if (!TextUtils.isEmpty(mWebView.getTitle())) {
                history.setTitle(mWebView.getTitle());
            } else {
                history.setTitle("首页");
            }
            history.setUrl(webView.getUrl());
            BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.default_logo);
            history.setIco(Utlis.GetBitmapByte(bd.getBitmap()));
            historyDao.insert(history);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void onMainAction(int acion) {
        try {
            switch (acion) {
                case 1://返回
                    if (mWebView != null) {
                        if (mWebView.canGoBack()) {
                            if (Is404 == true)
                                goBackInWebView(-2);//回退到倒数第二个页面
                            else
                                goBackInWebView(-1);
                        } else {
                            hidWeb();
                        }
                    }

                    break;
                case 2://前进
                    if (mWebView != null) {
                        if (isShowWeb) {
                            mWebView.goForward();
                        } else {
                            showWeb();
                        }
                    }
                    break;
                case 3://首页
                    //自定义网址
                    Config.isShowHome = true;
                    strUrl = String.valueOf(SharedPreferencesUtils.get(parentActivity, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html"));
                    showWeb();
                    break;
                case 4://刷新
                    if (isShowWeb) {
                        mWebView.reload();
                    }
                    break;
                case 5://添加书签
                    addBookMark();
                    break;
                case 6://系统后退按钮
                    if (!isShowWeb) {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else if (mWebView.canGoBack()) {//可以回退
                        if (Is404 == true)
                            goBackInWebView(-2);
                        else
                            goBackInWebView(-1);
                    } else if (isShowWeb) {
                        if (ISEXIT == false) {//在这里告知用户再点一次回退键则退出程序
                            ISEXIT = true;
                            exittext.setVisibility(View.VISIBLE);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(4000);//4秒之后提示消除
                                        ISEXIT = false;
                                        myHangler.post(new Runnable() {//这种方法相当于运行在主线程中
                                            @Override
                                            public void run() {
                                                exittext.setVisibility(View.GONE);
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            exittext.setVisibility(View.GONE);
                            parentActivity.moveTaskToBack(false);
                            hidWeb();
                        }
                    }
                    break;
                case 7://查找
                    //start(new LookupFragment());
                    break;
                case 8://停止刷新
                    if (mWebView != null) {
                        ISCOMPLETE = true;
                        mWebView.stopLoading();//停止刷新
                        refresh.setImageResource(R.mipmap.small_refresh);//修改 LOGO图样
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("xiaohuihui", e.toString());
        }
    }

    @OnClick(R.id.text_context)
    public void text_context(View view) {
        //点击首页的header搜索,跳转到搜索界面
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        if (mWebView != null) {
            String url = mWebView.getUrl();
            String home = (String) SharedPreferencesUtils.get(getActivity(), "home", "");
            if (url.equals(home))
                intent.putExtra("url", "");
            else
                intent.putExtra("url", mWebView.getUrl());
        }
        getActivity().startActivityForResult(intent, 0x456);
    }

    @SuppressLint("ResourceAsColor")
    @OnClick(R.id.menu)
    public void OnClickMenu() {
        final PopupWindow mPopWindow;
        View contentView = getActivity().getLayoutInflater().inflate(
                R.layout.menu_popuplayout, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //设置各个控件的点击响应
        TextView info_filter_text = (TextView) contentView.findViewById(R.id.info_filter_text);
        ImageView info_filter_img = (ImageView) contentView.findViewById(R.id.info_filter_img);
        if (Config.KeyWordFilter == true) {
            info_filter_text.setText("关闭不良信息屏蔽");
            info_filter_img.setImageResource(R.mipmap.item_6);
        } else {
            info_filter_text.setText("开启不良信息屏蔽");
            info_filter_img.setImageResource(R.mipmap.item_5);
        }

        LinearLayout visit_cihang = (LinearLayout) contentView.findViewById(R.id.visit_cihang);
        visit_cihang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadeWeb("http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html");
                mPopWindow.dismiss();
            }
        });

        LinearLayout info_filter = (LinearLayout) contentView.findViewById(R.id.info_filter);
        info_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.KeyWordFilter = !Config.KeyWordFilter;
                SharedPreferencesUtils.put(getActivity(), "keywordfilter", Config.KeyWordFilter);//关键字过滤
                if (Config.KeyWordFilter == true)
                    CustomToast.toast("开启成功");
                else
                    CustomToast.toast("关闭成功");
                mPopWindow.dismiss();
            }
        });
        TextView stop_filter_text = (TextView) contentView.findViewById(R.id.stop_filter_text);
        LinearLayout stop_filter = (LinearLayout) contentView.findViewById(R.id.stop_filter);
        if (stop_filter_text != null && (Config.KeyWordFilter == true || Config.NoImage == true)) {
            stop_filter.setEnabled(true);
            stop_filter_text.setTextColor(Color.parseColor("#7c7c7c"));
        } else {
            stop_filter.setEnabled(false);
            stop_filter_text.setTextColor(Color.parseColor("#a8a8a8"));
        }

        stop_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoFilter = NOFILTER;
                ImageNoFilter = NOFILTER;
                refresh();//刷新页面
                mPopWindow.dismiss();
            }
        });
        TextView stop_pic_mode_text = (TextView) contentView.findViewById(R.id.stop_pic_mode_text);
        LinearLayout stop_pic_mode = (LinearLayout) contentView.findViewById(R.id.stop_pic_mode);
        if (stop_pic_mode_text != null && Config.NoImage == false) {
            stop_pic_mode.setEnabled(false);
            stop_pic_mode_text.setTextColor(Color.parseColor("#a8a8a8"));
        } else {
            stop_pic_mode.setEnabled(true);
            stop_pic_mode_text.setTextColor(Color.parseColor("#7c7c7c"));
        }

        stop_pic_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageNoFilter = NOFILTER;
                refresh();//刷新页面
                mPopWindow.dismiss();
            }
        });

        LinearLayout addwhite = (LinearLayout) contentView.findViewById(R.id.addwhite);
        addwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(strUrl);
                String host = uri.getHost();
                UserWhiteUrl entity = userwhiteUrlDao.queryBuilder().where(UserWhiteUrlDao.Properties.Url.eq(host)).unique();
                if (entity == null) {
                    entity = new UserWhiteUrl();
                    entity.setUrl(host);
                    userwhiteUrlDao.insert(entity);
                }
                CustomToast.toast("白名单添加成功");
                mPopWindow.dismiss();
            }
        });
        LinearLayout addpicwhite = (LinearLayout) contentView.findViewById(R.id.addpicwhite);
        addpicwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mWebView.getUrl();
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    NoImageWhiteList entity = noImageWhiteListDao.queryBuilder().where(NoImageWhiteListDao.Properties.Url.eq(url)).unique();
                    if (entity == null) {
                        entity = new NoImageWhiteList();
                        entity.setUrl(url);
                        noImageWhiteListDao.insert(entity);
                        refresh();//刷新页面
                    }
                    CustomToast.toast("无图白名单添加成功");
                } else
                    CustomToast.toast("请输入正确网址");

                mPopWindow.dismiss();
            }
        });
        LinearLayout addblack = (LinearLayout) contentView.findViewById(R.id.addblack);
        addblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(strUrl);
                String host = uri.getHost();
                host = UrlFilter.HostDeleteHead(host);
                UserBlackUrl entity = userblackUrlDao.queryBuilder().where(UserBlackUrlDao.Properties.Url.eq(host)).unique();
                if (entity == null) {
                    entity = new UserBlackUrl();
                    entity.setUrl(host);
                    userblackUrlDao.insert(entity);
                    refresh();//刷新页面
                }
                CustomToast.toast("黑名单添加成功");
                mPopWindow.dismiss();
            }
        });
        LinearLayout addbookmark = (LinearLayout) contentView.findViewById(R.id.addbookmark);
        addbookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookMark();
                mPopWindow.dismiss();
            }
        });


        mPopWindow.setBackgroundDrawable(new ColorDrawable());//点击外部区域可以关闭
        mPopWindow.setTouchable(true);
        mPopWindow.showAsDropDown(menu, 0, 0);
    }

    @OnClick(R.id.refresh)
    public void refresh() {//点击刷新的位置
        if (ISCOMPLETE) {//如果是加载完成的则重新加载
            mWebView.reload();
        } else {//停止加载
            ISCOMPLETE = true;
            mWebView.stopLoading();
            refresh.setImageResource(R.mipmap.small_refresh);
        }

    }

    public boolean CanGoBack() {//查看是否可以回退
        if (mWebView.canGoBackOrForward(-1))
            return true;
        else
            return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        releaseWebViews();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        //onResume()是onPause()（通常是当前的acitivty被暂停了，比如被另一个透明或者Dialog样式的Activity覆盖了），之后dialog取消，activity回到可交互状态，调用onResume()。
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            setWebSize((int) SharedPreferencesUtils.get(getActivity(), "typeface", 3), mWebView.getSettings());
            mWebView.getSettings().setBlockNetworkImage(Config.NoImage);
        }
    }

    @Override
    public void onPause() {
        //当Activity被另一个透明或者Dialog样式的Activity覆盖时的状态。此时它依然与窗口管理器保持连接，系统继续维护其内部状态，它仍然可见，但它已经失去了焦点，故不可与用户交互。
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    private void setWebSize(int mUrlStartNum, WebSettings settings) {//设置网页文本大小
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

    public void loadeWeb(String url) {//加载页面
        strUrl = url;
        Config.isShowHome = false;  //是否为展示首页
        QRCode.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
        showWeb();
    }

    public void releaseWebViews() {
        if (null != mWebView) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    class MyHangler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            }
        }
    }
}
