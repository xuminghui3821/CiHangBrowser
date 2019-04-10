package org.fanhuang.cihangbrowser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.R2;
import org.fanhuang.cihangbrowser.activity.filtersetting.FilterSettingMainActivity;
import org.fanhuang.cihangbrowser.activity.setting.SettingAboutOurActivity;
import org.fanhuang.cihangbrowser.activity.setting.SettingMainActivity;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.entity.BookmarkEntity;
import org.fanhuang.cihangbrowser.entity.History;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.fragment.BrowserMainFragment;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.gen.DaoSession;
import org.fanhuang.cihangbrowser.gen.HistoryDao;
import org.fanhuang.cihangbrowser.interfaces.PopupCallback;
import org.fanhuang.cihangbrowser.interfaces.ShowWayListerCallBack;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.ShortcutUtils;
import org.fanhuang.cihangbrowser.adapter.PagerAdapter;
import org.fanhuang.cihangbrowser.adapter.MainRecyclerViewAdapter;
import org.fanhuang.cihangbrowser.utils.UpdateApp;
import org.fanhuang.cihangbrowser.view.PopupWindowView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;

import static org.fanhuang.cihangbrowser.filter.UrlFilter.ISChinaIP;


/**
 * Created by xiaohuihui on 2018/11/8.
 */

public class BrowserMainActivity extends SupportActivity implements BrowserMainFragment.OnItemOclickListers, ShowWayListerCallBack, PopupCallback {

    @BindView(R.id.leftbt_parent)
    RelativeLayout leftbtParent;
    @BindView(R.id.rightbt_parent)
    RelativeLayout rightbtParent;
    @BindView(R.id.homebt_parent)
    RelativeLayout homebtParent;
    @BindView(R.id.pagebt_parent)
    RelativeLayout pagebtParent;
    @BindView(R.id.setbt_parent)
    RelativeLayout setbtParent;
    @BindView(R.id.llayoutviewpage)
    FrameLayout llayoutviewpage;
    @BindView(R.id.leftbt)
    ImageView leftbt;
    @BindView(R.id.rightbt)
    ImageView rightbt;
    @BindView(R.id.setbt)
    ImageView setbt;
    @BindView(R.id.pagebt)
    TextView pagebt;
    @BindView(R.id.homebt)
    ImageView homebt;
    @BindView(R.id.mainbarlt)
    LinearLayout mainbarlt;
    @BindView(R.id.fragment_content)
    RelativeLayout fragmentContent;
    @BindView(R.id.chooseAll)//添加新页面界面的关闭全部按钮
            RelativeLayout chooseAll;
    @BindView(R.id.addnewpage)//添加新页面界面的新建窗口按钮
            RelativeLayout addnewpage;
    @BindView(R.id.returnmain)//添加新页面界面的返回按钮
            RelativeLayout returnmain;
    @BindView(R.id.page_recyclerView)
    RecyclerView pageRecyclerView;
    @BindView(R.id.fragment_paging)
    RelativeLayout fragmentPaging;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<BrowserMainFragment> mFragments = new ArrayList<>();
    private PopupWindowView popupWindowView;//弹出窗口 点设置按钮弹出的界面
    private String Refresh = "";
    private int mCurrentPosition = 0;//用来记录当前打开的是第几个页面
    private LinearLayoutManager manager;
    private PagerAdapter mAdapter;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    public String externalurl = "";//从外部调用浏览器


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//使窗口支持透明度
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
        setContentView(R.layout.activity_browser_main);
        ButterKnife.bind(this);
        new UpdateApp(this);//检查更新
        viewInit();
        Uri uri = getIntent().getData();
        if (uri != null) {
            externalurl = uri.toString();
        }
        mFragments.add(BrowserMainFragment.newInstance());
        loadRootFragment(R.id.llayoutviewpage, mFragments.get(mFragments.size() - 1));
    }

    private void viewInit() {
        popupWindowView = new PopupWindowView(this);
        //pagebarlt = (LinearLayout) findViewById(R.id.pagebarlt);
//        new LinearSnapHelper().attachToRecyclerView(pageRecyclerView);
        rightbt.setBackgroundResource(R.mipmap.bottom_go_tint);
        leftbt.setBackgroundResource(R.mipmap.bottom_back_tint);
        //        leftbt.setBackground(this.getResources().getDrawable(R.mipmap.bottom_back));
        //        rightbt.setBackground(this.getResources().getDrawable(R.mipmap.bottom_go));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //1、通过Intent启到一个Activity的时候，就算已经存在一个相同的正在运行的Activity,系统也会创建一个新的Activity实例。为了不让Activity实例化多次，我们需要通过在AndroidManifest.xml配置启动方式。
        //2、launchMode为singleTask的时候，通过Intent跳到一个Activity,如果系统已经存在一个实例，系统就会将请求发送到这个实例上，但这个时候----------系统就不会再调用onCreate方法，而是调用onNewIntent方法。
        super.onNewIntent(intent);
        if(intent != null) {
            if (intent.getAction().equals("android.intent.action.VIEW")) {//从外部吊起的，打开一个页面
                String url = intent.getData().toString();
                mFragments.get(mCurrentPosition).loadeWeb(url);
            } else if (intent.getIntExtra("action", 0) == 0) {
                if (!TextUtils.isEmpty(intent.getStringExtra("url"))) {
                    mFragments.get(mCurrentPosition).loadeWeb(intent.getStringExtra("url"));
                }
            } else {
                if (!TextUtils.isEmpty(intent.getStringExtra("url"))) {
                    mFragments.get(mCurrentPosition).loadeWeb("file://" + intent.getStringExtra("url"));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//请求权限的结果回调
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
        if (requestCode == 0x01) {//创建桌面快捷方式的权限
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权成功
                mFragments.get(mCurrentPosition).dinwei();
            } else {//授权失败
                Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @param requestCode  标识
     * @param grantResults 用户获取的权限信息
     */
    private void doNext(final int requestCode, final int[] grantResults) {
        try {
            if (requestCode == 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    CustomToast.toast("权限获取失败，有些功能将无法使用");
                }
            } else if (requestCode == 1) {
                shortcutDialog();
            } else if (requestCode == 6) {//相机权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.startActivityForResult(new Intent(this, ScanQRcodeActivity.class), 0x456);
                } else {
                    CustomToast.toast("请赋予权限");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPager() {
        try {
            List<Bitmap> Logos = new ArrayList<>();
            List<Bitmap> bitmaps = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> address = new ArrayList<>();
            // TODO: 2017/7/14
            for (int i = 0; i < mFragments.size(); i++) {
                View view = mFragments.get(i).getView();        //获取fragment的view
                if (view != null) {
                    view.buildDrawingCache();                   //对view进行截图
                    view.setDrawingCacheEnabled(true);          //设置view可截图
                    //先对bitmap进行压缩
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);//缩放处理 高和宽都为之前的一半
                    Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getDrawingCache().getWidth(),
                            view.getDrawingCache().getHeight(), matrix, true);

                    //                    Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());   //view.getDrawingCache()会触发buildDrawingCache();
                    view.destroyDrawingCache();
                    bitmaps.add(bmp);
                }
                titles.add(mFragments.get(i).getTilte());
                address.add(mFragments.get(i).getAddress());
                Logos.add(mFragments.get(i).GetsitesLogo());
            }
            manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            pageRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new org.fanhuang.cihangbrowser.adapter.PagerAdapter(this, Logos, bitmaps, titles, address, mCurrentPosition);
            pageRecyclerView.setLayoutManager(manager);
            pageRecyclerView.setAdapter(mAdapter);
            mAdapter.setmCallBack(this);
            //            Utlis.MoveToPosition(manager, pageRecyclerView, viewPager.getCurrentItem());
            fragmentContent.setVisibility(View.GONE);               //浏览器界面隐藏
            fragmentPaging.setVisibility(View.VISIBLE);             //新建窗口界面(全屏),背景设为纯黑,实现界面进入后台的效果
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mainRecyclerViewAdapter = new MainRecyclerViewAdapter(this, titles, mCurrentPosition);
            recyclerView.setAdapter(mainRecyclerViewAdapter);
            pageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {//滑动页面事件 并对mainRecyclerViewAdapter最相应的更新
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        mainRecyclerViewAdapter.chageview(lastVisibleItem);
                    }
                }
            });
        } catch (Exception E) {
        }
    }


    /**
     * 创建对话框.
     */
    private void shortcutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("为了正常使用,我们需要必要的权限");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shortcutJurisdiction();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void shortcutJurisdiction() {//创建桌面快捷方式
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INSTALL_SHORTCUT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, 1);
        } else {
            this.sendBroadcast(ShortcutUtils.getShortcutToDesktopIntent(this, mFragments.get(mCurrentPosition).getTilte(), null, mFragments.get(mCurrentPosition).getStrUrl()));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: UNBIND
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    //通过判断webview是否能"前进"和"后退"来设置图片
    public void setImageSrc(int act) {
        switch (act) {
            case 0:
                //webview可以返回
                leftbtParent.setClickable(true);
                leftbt.setBackgroundResource(R.mipmap.bottom_back_tint);
                break;
            case 1:
                //webview不可返回\
                if (mFragments.size() == 1) {
                    leftbtParent.setClickable(false);
                    leftbt.setBackgroundResource(R.mipmap.bottom_back_forbidden);
                } else {
                    leftbtParent.setClickable(true);
                    leftbt.setBackgroundResource(R.mipmap.bottom_back_tint);
                }
                break;
            case 2:
                //webview可以前进
                rightbtParent.setClickable(true);
                rightbt.setBackgroundResource(R.mipmap.bottom_go_tint);
                break;
            case 3:
                //webview不可前进
                rightbtParent.setClickable(false);
                rightbt.setBackgroundResource(R.mipmap.bottom_go_forbidden);
                break;
        }
    }

    public void addFragment() {
        mFragments.add(BrowserMainFragment.newInstance());
        loadRootFragment(R.id.llayoutviewpage, mFragments.get(mFragments.size() - 1));
        fragmentContent.setVisibility(View.VISIBLE);
        fragmentPaging.setVisibility(View.GONE);
        rightbt.setBackgroundResource(R.mipmap.bottom_go_tint);
        leftbt.setBackgroundResource(R.mipmap.bottom_back_tint);
        showPage();
        mCurrentPosition = mFragments.size() - 1;
    }

    private void showPage() {//显示当前有几个页面
        pagebt.setText(String.valueOf(mFragments.size()));
    }

    @OnClick(R.id.leftbt_parent)//后退
    public void leftbt(View view) {
        if (mFragments.get(mCurrentPosition).CanGoBack())//如果可以回退
            mFragments.get(mCurrentPosition).onMainAction(1);
        else {
            if (mFragments.size() > 1) {
                BrowserMainFragment temp = mFragments.get(mCurrentPosition);//记录准备移除的fragment
                mFragments.remove(mCurrentPosition);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(temp);//删除这个对象
                transaction.commit();//执行
                mCurrentPosition = mFragments.size() - 1;
                showHideFragment(mFragments.get(mCurrentPosition));
                showPage();
                if (mFragments.size() == 1) {//当前只剩下一个页面则将后退按钮设置为不可用
                    setImageSrc(1);
                }
            }
        }
    }

    @OnClick(R.id.rightbt_parent)//前进
    public void rightbt(View view) {
//        if (Refresh != null && Refresh.equals("Refresh")) {
//            mFragments.get(mCurrentPosition).onMainAction(8);
//        } else
        {
            mFragments.get(mCurrentPosition).onMainAction(2);
        }
    }

    @OnClick(R.id.setbt_parent)//菜单
    public void setbt(View view) {
        popupWindowView.showUp2(llayoutviewpage, setbt, mFragments.get(mCurrentPosition).isShowWeb);
        setbt.setBackgroundResource(R.mipmap.main_set_check);//设置按钮颜色

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//按下按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {//是退出按钮
            if (fragmentContent.getVisibility() == View.GONE) {//多页面选项页面
                fragmentContent.setVisibility(View.VISIBLE);
                fragmentPaging.setVisibility(View.GONE);
            } else {
                mFragments.get(mCurrentPosition).onMainAction(6);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.pagebt_parent)//查看所有页面
    public void pagebt(View view) {
        startPager();
    }


    void SearchDialog() {//搜索栏dialog
        View view = LayoutInflater.from(this).inflate(R.layout.search_dialog, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        mDialog.setCancelable(true);
        final EditText editText = (EditText) view.findViewById(R.id.e_tv);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragments.get(mCurrentPosition).lookUp(editText.getText().toString());
                mDialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void popupCallback(int id) {//菜单页
        switch (id) {
            case R.id.pop_mymark_parent:
                Intent intent_bookmark = new Intent(this, NewBookMarkAndHistoryActivity.class);
                intent_bookmark.putExtra("state", "bookmark");
                startActivity(intent_bookmark);
                break;
            case R.id.pop_history_parent:
                Intent intent_history = new Intent(this, NewBookMarkAndHistoryActivity.class);
                intent_history.putExtra("state", "history");
                startActivity(intent_history);
                break;
            case R.id.pop_down_parent:
                startActivity(new Intent(this, NewDownloadActiviy.class));
                break;
            case R.id.pop_seacher_parent:
                SearchDialog();
                break;
            case R.id.pop_addmark_parent:
                mFragments.get(mCurrentPosition).onMainAction(5);
                break;
            case R.id.pop_refresh_parent:
                startActivityForResult(new Intent(this, FilterSettingMainActivity.class), 666);
                break;
            case R.id.pop_set_parent:

                startActivityForResult(new Intent(this, SettingMainActivity.class), 999);
                break;
            case R.id.pop_exite_parent:
                System.exit(0);//正常退出App
                this.finish();
                break;
        }
    }

    @Override
    public void showPager(int action, int postion) {
        try {
            mCurrentPosition = postion;
            if (action == 1) {//点击的图片
                showHideFragment(mFragments.get(postion));
                fragmentContent.setVisibility(View.VISIBLE);
                fragmentPaging.setVisibility(View.GONE);
                //mFragments.get(mCurrentPosition).showStae();
            } else if (action == 2) {
                if (mFragments.size() > 1) {
                    BrowserMainFragment temp = mFragments.get(postion);//记录准备移除的fragment
                    mFragments.remove(postion);//从列表中移除
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(temp);//删除这个对象
                    transaction.commit();//执行
                    mCurrentPosition = mFragments.size() - 1;
                    fragmentContent.setVisibility(View.VISIBLE);
                    fragmentPaging.setVisibility(View.GONE);
                    showHideFragment(mFragments.get(mCurrentPosition));
                    showPage();
                } else {
                    CustomToast.toast("唯一页不能被删除！");
                    fragmentContent.setVisibility(View.VISIBLE);
                    fragmentPaging.setVisibility(View.GONE);
                }
            }
        } catch (Exception E) {

        }
    }

    @OnClick(R.id.chooseAll)//关闭全部
    public void chooseAll(View view) {
        try {
            if (mFragments.size() > 1) {
                for (int i = 0; i < mFragments.size(); i++) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(mFragments.get(i));
                    transaction.commit();
                    mFragments.get(i).pop();
                }
                mFragments.clear();
                mFragments.add(BrowserMainFragment.newInstance());
                loadRootFragment(R.id.llayoutviewpage, mFragments.get(0));
                showPage();
            }
            mCurrentPosition = 0;
            fragmentContent.setVisibility(View.VISIBLE);
            fragmentPaging.setVisibility(View.GONE);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @OnClick(R.id.addnewpage)//新增窗口
    public void addnewpage(View view) {
        if (mFragments.size() < 10) {
            addFragment();
            //mFragments.get(mCurrentPosition).showStae();
        } else {
            Toast.makeText(getApplicationContext(), "已达窗口上限", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.returnmain)//返回
    public void returnmain(View view) {
        fragmentContent.setVisibility(View.VISIBLE);
        fragmentPaging.setVisibility(View.GONE);
    }

    @OnClick(R.id.homebt_parent)//首页
    public void homebt(View view) {
        mFragments.get(mCurrentPosition).onMainAction(3);
    }


    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x123) {//搜索网页关键字返回的
            mFragments.get(mCurrentPosition).lookUp(data.getStringExtra("look"));
        } else if (resultCode == 5566 && requestCode == 0x456) {//SearchActivity.java setOnItemClickListener函数返回
            mFragments.get(mCurrentPosition).loadeWeb(data.getStringExtra("url"));
        } else if (resultCode == RESULT_OK && requestCode == 0x456) {//SearchAcitvity  @OnClick(R.id.logo)方法返回
            mFragments.get(mCurrentPosition).loadeWeb(data.getStringExtra("url"));
        } else if (resultCode == 999 && requestCode == 999) {
            mFragments.get(mCurrentPosition).loadeWeb(data.getStringExtra("url"));
        }

    }


    @Override
    public void AddFragment(String url) {
        externalurl = url;
        addFragment();
    }
}
