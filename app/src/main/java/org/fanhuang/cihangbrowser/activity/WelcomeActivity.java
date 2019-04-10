package org.fanhuang.cihangbrowser.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.rollviewpager.HintView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;


import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.viewPager)
    RollPagerView viewPager;
    @BindView(R.id.welcome_tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);//这里必须要绑定
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {//如果安卓版本超过11
                getWindow()
                        .setFlags(
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,//硬件加速
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        tv.setOnClickListener(this);
        viewPager.setHintView(null);//隐藏指示器（滑动图片下面的小点）
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnItemClickListener(new OnItemClickListener() {//列表元素点击
            @Override
            public void onItemClick(int position) {//当点击第二个元素时
                if(position == 2)
                    tv.setVisibility(View.VISIBLE);
            }
        });
        viewPager.setHintViewDelegate(new RollPagerView.HintViewDelegate() {
            @Override
            public void setCurrentPosition(int position, HintView hintView) {
                if(position == 2){
                    tv.setVisibility(View.VISIBLE);
                }else {
                    tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void initView(int length, int gravity, HintView hintView) {

            }
        });
        choseActivity();//申请权限

        if ((boolean) SharedPreferencesUtils.get(this, "introduce", false)) {//跳过轮播页
            startActivity(new Intent(WelcomeActivity.this, BrowserMainActivity.class));
            WelcomeActivity.this.finish();
        }
    }
    class MyPagerAdapter extends StaticPagerAdapter{
        private int[] image = {R.mipmap.img1, R.mipmap.img2, R.mipmap.img3};

        // SetScaleType(ImageView.ScaleType.CENTER_CROP);
        // 按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
        @Override
        public View getView(ViewGroup container, int position) {//获取position下标所对应的图片
            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageResource(image[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);//图片平铺
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;

        }

        @Override
        public int getCount() {
            return image.length;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.welcome_tv:
                SharedPreferencesUtils.put(WelcomeActivity.this, "introduce", true);
                SharedPreferencesUtils.put(WelcomeActivity.this, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html");//设置默认主页
                startActivity(new Intent(WelcomeActivity.this, BrowserMainActivity.class));
                WelcomeActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //让view置空
//        setContentView(R.layout.view_null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权成功
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
        } else {//授权失败
            Toast.makeText(this, "获取权限失败,部分功能将影响使用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 提示用户给权限
     */
    private void choseActivity() {
        //WRITE_EXTERNAL_STORAGE这个权限已经在AndroidManifest中申请了，正常情况下内部的函数是不会执行的
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//WRITE_EXTERNAL_STORAGE写外部存储权限
            //Toast.makeText(getApplicationContext(), "为了保证你的正常使用，请赋予以下权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_LOGS,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.RESTART_PACKAGES,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.GET_TASKS,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.MANAGE_DOCUMENTS,
                            Manifest.permission.READ_SYNC_SETTINGS,
                            Manifest.permission.UNINSTALL_SHORTCUT,
                            Manifest.permission.INSTALL_SHORTCUT,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.RECEIVE_BOOT_COMPLETED,
                            Manifest.permission.CAMERA
                    },
                    0);
        }
    }
}
