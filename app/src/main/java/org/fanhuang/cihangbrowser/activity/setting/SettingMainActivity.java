package org.fanhuang.cihangbrowser.activity.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.ListView;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.BrowserMainActivity;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.gen.HistoryDao;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.KeyBoardUtils;
import org.fanhuang.cihangbrowser.utils.X5WebView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/7.
 */

public class SettingMainActivity extends AppCompatActivity {
    @BindView(R.id.back)
    MaterialRippleLayout back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.homepage)
    TextView homeurl;
    @BindView(R.id.noImg)
    LinearLayout noImg;
    @BindView(R.id.fontSize)
    TextView fontSize;
    @BindView(R.id.now_search)
    TextView now_search;
    @BindView(R.id.setHome)
    RelativeLayout setHome;
    @BindView(R.id.setSearch)
    RelativeLayout setSearch;
    @BindView(R.id.edition)
    TextView edition;
    @BindView(R.id.noImgIcon)
    ImageView noImgIcon;
    @BindView(R.id.wisdom)
    TextView wisdom;
    @BindView(R.id.aboutour)
    TextView aboutour;
    @BindView(R.id.default_browser)
    TextView default_browser;


    private int type = 0;
    private String HomePage="";
    private int num = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main_activiy);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        title.setText("设置");
        HomePage = (String)SharedPreferencesUtils.get(this, "home", "");
        type = (int) SharedPreferencesUtils.get(this, "typeface", 3);
        if (Config.NoImage) {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
        } else {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
        }
        if(!HomePage.isEmpty()){
            homeurl.setText(HomePage);
        }

       String Search = (String)SharedPreferencesUtils.get(SettingMainActivity.this,"search","");//设置搜索链接
        if(Search.contains("cn.bing.com"))
            now_search.setText("必应");
        else if(Search.contains("m.baidu.com"))
            now_search.setText("百度");
        else if(Search.contains("wenku"))
            now_search.setText("慈航文库");
    }

    @OnClick(R.id.back)
    public void back() {
        this.finish();
    }

    //接收返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1 && requestCode == 1){//requestCode是调用窗口传递给被调用窗口的参数,resultCode是被调用窗口返回给调用窗口的值
            Intent intent = new Intent();
            intent.putExtra("url",data.getStringExtra("url"));
            setResult(999,intent);
            this.finish();
        }
    }

    @OnClick(R.id.noImg)
    public void noImg() {
        Config.NoImage = !Config.NoImage;
        SharedPreferencesUtils.put(this, "no_img", Config.NoImage);
        if (Config.NoImage) {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_on);
            CustomToast.toast("已开启无图模式");
        } else {
            noImgIcon.setBackgroundResource(R.mipmap.ic_setup_switch_off);
            CustomToast.toast("已关闭无图模式");
        }
    }

    @OnClick(R.id.wisdom)
    public void wisdom(){
        startActivity(new Intent(this, SettingWisdomActivity.class));
    }


    @OnClick(R.id.edition)
    public void edition() {
        startActivity(new Intent(this, SettingUpdataActivity.class));
    }


    @OnClick(R.id.aboutour)
    public void about() {
        Intent in = new Intent(this,SettingAboutOurActivity.class);
        startActivityForResult(in,1);//被调用的窗口返回数据给调用窗口
    }

    @OnClick(R.id.default_browser)
    public void SetDefaultBrowser(){
        startActivityForResult( new Intent(this,SetDefaultBrowserActivity.class),1);//被调用的窗口返回数据给调用窗口
    }


    @OnClick(R.id.fontSize)
    public void fontSize() {
        startActivity(new Intent(this, SettingTypefaceActivity.class));
    }

    class MyFontSizeAdapter extends BaseAdapter {
        private String[] fontsize = { "小", "正常(建议)", "大"};

        public MyFontSizeAdapter() {
            type = (int) SharedPreferencesUtils.get(SettingMainActivity.this, "typeface", 3);
        }

        @Override
        public int getCount() {
            return fontsize.length;
        }

        @Override
        public Object getItem(int position) {
            return fontsize[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SettingMainActivity.this).inflate(R.layout.item_font_size, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.font.setText(fontsize[position]);
            if (type == position + 1) {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.base_color));
                holder.icon.setVisibility(View.VISIBLE);
            } else {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.black));
                holder.icon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.icon)
            ImageView icon;
            @BindView(R.id.font)
            TextView font;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @OnClick(R.id.clean_pass)
    public void cleanpass(View view) {
        //清空所有Cookie
        CookieSyncManager.createInstance(getApplicationContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
        CustomToast.toast("清除成功");
    }

    @OnClick(R.id.clean_data)
    public void cleandata(View view) {
        //清除缓存
        X5WebView  mWebView = new X5WebView(this, null);
        mWebView.clearCache(true);//清除缓存
        CustomToast.toast("清除成功");
    }


    @OnClick(R.id.yincang)
    public void OnClickYincang(){
        num++;
        if(num == 10){
            num = 0;
            setHome();
        }
    }

    @OnClick(R.id.setSearch)
    public void setSearch() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        final View views = LayoutInflater.from(this).inflate(R.layout.view_font_size, null);
        bottomSheetDialog.contentView(views);
        bottomSheetDialog.inDuration(200);
        bottomSheetDialog.outDuration(200);
        bottomSheetDialog.cancelable(true);
        bottomSheetDialog.show();
        CompoundButton button = (CompoundButton) views.findViewById(R.id.chose);
        TextView textView = (TextView) views.findViewById(R.id.name);
        textView.setText("搜索引擎");
        ListView listView = (ListView) views.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {//必应
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this,"search","https://cn.bing.com/search?q=");//设置搜索链接
                        now_search.setText("必应");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    case 1: {//百度
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this,"search","https://m.baidu.com/s?word=");//设置搜索链接
                        now_search.setText("百度");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    case 2: {//慈航
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this,"search","http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html?keyword=");//设置搜索链接
                        now_search.setText("慈航");
                        CustomToast.toast("设置成功");
                        break;
                    }
                }
            }
        });
        listView.setAdapter(new MySetSearch());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }



    @OnClick(R.id.setHome)
    public void setHome() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        final View views = LayoutInflater.from(this).inflate(R.layout.view_font_size, null);
        bottomSheetDialog.contentView(views);
        bottomSheetDialog.inDuration(200);
        bottomSheetDialog.outDuration(200);
        bottomSheetDialog.cancelable(true);
        bottomSheetDialog.show();
        CompoundButton button = (CompoundButton) views.findViewById(R.id.chose);
        TextView textView = (TextView) views.findViewById(R.id.name);
        textView.setText("设置主页");
        ListView listView = (ListView) views.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:{//必应
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this, "home", "https://cn.bing.com/");
                        homeurl.setText("https://cn.bing.com/");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    case 1: {//百度
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this, "home", "https://m.baidu.com/?wpo=btmfast&pu=sz%401321_666");
                        homeurl.setText("https://m.baidu.com/?wpo=btmfast&pu=sz%401321_666");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    case 2:{
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html");
                        homeurl.setText("http://tieba.baidu.com/f?kw=%BD%E4%C9%AB&fr=ala0&loc=rec");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    case 3: {//戒色吧
                        bottomSheetDialog.dismiss();
                        SharedPreferencesUtils.put(SettingMainActivity.this, "home", "http://tieba.baidu.com/f?kw=%BD%E4%C9%AB&fr=ala0&loc=rec");
                        homeurl.setText("http://tieba.baidu.com/f?kw=%BD%E4%C9%AB&fr=ala0&loc=rec");
                        CustomToast.toast("设置成功");
                        break;
                    }
                    default:{//自定义模式
                            bottomSheetDialog.dismiss();
                            final Dialog dialog = new AlertDialog.Builder(SettingMainActivity.this).create();
                            final View views = LayoutInflater.from(SettingMainActivity.this).inflate(R.layout.view_set_home, null);
                            dialog.show();
                            dialog.getWindow().setContentView(views);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setGravity(Gravity.BOTTOM);
                            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(INPUT_METHOD_SERVICE);
                            imm.showSoftInput(views, 0); //显示软键盘
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //显示软键盘
                            TextView button = (TextView) views.findViewById(R.id.cancel);
                            TextView button1 = (TextView) views.findViewById(R.id.query);
                            final EditText home = (EditText) views.findViewById(R.id.home);
                            home.setText(String.valueOf(SharedPreferencesUtils.get(SettingMainActivity.this, "home", "https://cn.bing.com/")));
                            home.setSelection(home.getText().toString().trim().length());
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    KeyBoardUtils.closeKeybord(home, getApplicationContext());
                                    dialog.dismiss();
                                }
                            });
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strHome = home.getText().toString().trim();
                                    if (Patterns.WEB_URL.matcher(strHome).matches()) {
                                        if (strHome.contains("http")) {
                                            SharedPreferencesUtils.put(SettingMainActivity.this, "home", strHome);
                                            homeurl.setText(strHome);
                                        } else {
                                            String a = "http://" + strHome;
                                            SharedPreferencesUtils.put(SettingMainActivity.this, "home", a);
                                            homeurl.setText(a);
                                        }
                                        CustomToast.toast("设置成功");
                                        KeyBoardUtils.closeKeybord(home, getApplicationContext());
                                        dialog.dismiss();
                                    } else {
                                        CustomToast.toast("请输入有效网址！");
                                    }

                                }
                            });
                        break;
                    }
                }
            }
        });
        listView.setAdapter(new MySetHom());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


    }


    class MySetHom extends BaseAdapter {
        private String[] fontsize = {"必应搜索","百度搜索","慈航首页","戒色吧", "自定义"};
        private String home = "";
        private int type;

        public MySetHom() {
            home = String.valueOf(SharedPreferencesUtils.get(SettingMainActivity.this, "home", "https://cn.bing.com/"));
            if (home.contains("https://cn.bing.com/")) {
                type = 0;
            } else if(home.contains("https://m.baidu.com/?wpo=btmfast&pu=sz%401321_480")){
                type = 1;
            }else if(home.contains("http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html")){
                type = 2;
            }else if(home.contains("http://tieba.baidu.com/f?kw=%BD%E4%C9%AB&fr=ala0&loc=rec")){
                type = 3;
            }
            else type = 4;
        }

        @Override
        public int getCount() {
            return fontsize.length;
        }

        @Override
        public Object getItem(int position) {
            return fontsize[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SettingMainActivity.this).inflate(R.layout.item_font_size, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.font.setText(fontsize[position]);
            if (position == type) {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.base_color));
                holder.icon.setVisibility(View.VISIBLE);
            } else {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.black));
                holder.icon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.icon)
            ImageView icon;
            @BindView(R.id.font)
            TextView font;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class MySetSearch extends BaseAdapter {
        private String[] Searchsize = {"必应搜索","百度搜索","慈航文库"};
        private String url = "";
        private int type;

        public MySetSearch() {
            url = String.valueOf(SharedPreferencesUtils.get(SettingMainActivity.this, "search", "https://cn.bing.com/search?q="));
            if (url.contains("bing")) {
                type = 0;
            } else if(url.contains("baidu")){
                type = 1;
            }else if(url.contains("wenku")){
                type = 2;
            }
        }

        @Override
        public int getCount() {
            return Searchsize.length;
        }

        @Override
        public Object getItem(int position) {
            return Searchsize[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SettingMainActivity.this).inflate(R.layout.item_font_size, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.font.setText(Searchsize[position]);
            if (position == type) {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.base_color));
                holder.icon.setVisibility(View.VISIBLE);
            } else {
                holder.font.setTextColor(SettingMainActivity.this.getResources().getColor(R.color.black));
                holder.icon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.icon)
            ImageView icon;
            @BindView(R.id.font)
            TextView font;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    public Bitmap getThumbnail(Uri uri, int size) throws FileNotFoundException, IOException {
        //通过uri拿取到输入流
        InputStream input = getContentResolver().openInputStream(uri);
        //拿到Option对象, 设置一些优化的属性
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);//从输入流中解码图片,得到图片的option
        input.close();//关闭输入流
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)){
            return null;
        }
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;//取款高的最大值
        //得到给定的大小和原始大小的比值
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);//再次得到输入流
        //根据缩放后的Option得到bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
}
