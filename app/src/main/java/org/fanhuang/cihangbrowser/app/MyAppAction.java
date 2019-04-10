package org.fanhuang.cihangbrowser.app;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;

import org.fanhuang.cihangbrowser.R2;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.entity.KeyWordList;
import org.fanhuang.cihangbrowser.filter.AdblockPlus.AdblockPlus;
import org.fanhuang.cihangbrowser.filter.KeyWordHash;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.DaoMaster;
import org.fanhuang.cihangbrowser.gen.DaoSession;
import org.fanhuang.cihangbrowser.gen.KeyWordListDao;
import org.fanhuang.cihangbrowser.network.Config;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * Created by xiaohuihui on 2018/10/29.
 */
//当文件的方法数超过65535时就要用MultiDexApplication
//否则使用 Application
public class MyAppAction extends MultiDexApplication {

    public static Context _Contenxt;
    public static MyAppAction instances;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    public DaoSession mDaoSession;

    public Boolean mChinaIPInit = false;//国内IP初始化是否结束
    public Boolean mBlackUrlInit = false;//网站黑名单是否初始化完毕
    public Boolean KeyWordHashInit = false;//关键字名单是否初始化完毕
    public List<ChinaIP> mAllIP = new ArrayList<>();
    public List<String> BlackUrl = new ArrayList<>();
    public KeyWordHash keyWordHash = null;

    public AdblockPlus adblockPlus = null;
    @Override
    public void onCreate() {
        super.onCreate();
        _Contenxt = getApplicationContext();//返回应用的上下文
        instances = this;
        setDatabase();//初始化数据库 数据库初始化工作必须要在ConfigInit之前进行，因为在ConfigInit中有数据库操作
        ConfigInit();//配置文件初始化
        new Thread(){//由于读取数据文件会浪费大量的时间所以放到线程中进行处理
           public void run(){
               fanhuang();//各种反黄相关的关键字进行初始化
            }
        }.start();
        InitBugly();//Bugly的所有操作
        InitX5();//初始化腾讯X5
        OtherInit();//其他功能初始化
    }

    private void InitAdblock(){
        try {

            adblockPlus = new AdblockPlus();
        }catch (Exception e){

            e.printStackTrace();
        }
    }

    private void fanhuang(){
        InitChinaIP();//初始化IP
        InitKeyWord();
        InitBlackUrl();//初始化网站黑名单
        InitAdblock();//初始化广告规则
    }

    private void OtherInit(){
        x.Ext.init(this);//初始化工具模块
        x.Ext.setDebug(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void InitX5()//初始化腾讯X5浏览器内核
    {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);//这里会申请定位权限
    }

    public static MyAppAction getInstances() {
        return instances;
    }


    private void InitChinaIP(){//如果是软件第一次打开则初始化中国境内IP数据库
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("FilterSrc/ChinaIP.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                String[] Head_Tail = line.split("\t");
                if(Head_Tail.length == 2){
                    Long head = Long.parseLong(Head_Tail[0]);
                    Long tail = Long.parseLong(Head_Tail[1]);
                    if (head < tail) {//将该IP号段放入到数据库中
                        ChinaIP chinaip = new ChinaIP();
                        chinaip.min = head;
                        chinaip.max = tail;
                        mAllIP.add(chinaip);
                    }
                }
            }
            mChinaIPInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitBlackUrl(){//如果是软件第一次打开则初始化中国境内IP数据库
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("FilterSrc/BlackUrl.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                BlackUrl.add(line);
            mBlackUrlInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void InitKeyWord(){
        try {
            keyWordHash = new KeyWordHash();
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("FilterSrc/KeyWord.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String keyword = "";
            while ((keyword = bufReader.readLine()) != null)
                keyWordHash.add(keyword,0);
            inputReader = new InputStreamReader(getResources().getAssets().open("FilterSrc/StrickKeyWord.txt"));
            bufReader = new BufferedReader(inputReader);
            while ((keyword = bufReader.readLine()) != null)
                keyWordHash.add(keyword,1);

            KeyWordListDao keyWordListDao = MyAppAction.getInstances().getDaoSession().getKeyWordListDao();
            List<KeyWordList> keyWordLists = keyWordListDao.queryBuilder().orderAsc(KeyWordListDao.Properties.Keyword).list();//获取用户定义的关键字
            for(int i = 0; i < keyWordLists.size();i++)
                keyWordHash.add(keyWordLists.get(i).getKeyword(),0);

            KeyWordHashInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ConfigInit() {
        boolean first = (boolean) SharedPreferencesUtils.get(this, "first", true);
        if (first == true) {//浏览器是第一次打开
            SharedPreferencesUtils.put(this, "home", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html");//设置浏览器主页
            SharedPreferencesUtils.put(this, "first", false);//设置是否是第一次打开
            SharedPreferencesUtils.put(this, "search", "http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html?keyword=");//设置搜索链接
            SharedPreferencesUtils.put(this, "autoupdata", false);//自动更新默认为关闭，在版本介绍中打开
            SharedPreferencesUtils.put(this, "filter_outside_ip", false);//屏蔽国外链接按钮
            SharedPreferencesUtils.put(this, "filtermode", false);//过滤模式
            SharedPreferencesUtils.put(this, "powerfulfilter", false);//强力过滤
            SharedPreferencesUtils.put(this, "no_img", false);//无图模式
            SharedPreferencesUtils.put(this, "keywordfilter", false);//关键字过滤
            SharedPreferencesUtils.put(this, "filternum", 0);//过滤的个数
            SharedPreferencesUtils.put(this, "contentfilter", false);//关键字过滤
            SharedPreferencesUtils.put(this, "guanggaofilter", false);//广告过滤
            SharedPreferencesUtils.put(this, "guanggaofilternum", 0);//广告过滤
        }
        Config.FilterOutsideIPFlag = (boolean)SharedPreferencesUtils.get(this, "filter_outside_ip", false);//屏蔽国外链接按钮
        Config.FilterMode = (boolean)SharedPreferencesUtils.get(this, "filtermode", false);//屏蔽国外链接按钮
        Config.NoImage = (boolean)SharedPreferencesUtils.get(this, "no_img", false);//无图模式
        Config.KeyWordFilter = (boolean)SharedPreferencesUtils.get(this, "keywordfilter", false);//关键字过滤
        Config.PowerfulFilter = (boolean)SharedPreferencesUtils.get(this, "powerfulfilter", false);//强力过滤
        Config.FilterNum = (int)SharedPreferencesUtils.get(this, "filternum", 0);//强力过滤
        Config.ContentFilter=(boolean)SharedPreferencesUtils.get(this, "contentfilter", false);//网站内容过滤
        Config.GuanggaoFilter=(boolean)SharedPreferencesUtils.get(this, "guanggaofilter", false);//网站内容过滤
        Config.ADVFilterNum=(int)SharedPreferencesUtils.get(this, "guanggaofilternum", 0);//网站内容过滤
    }

    private void InitBugly() {//初始化Butly的各个参数
        Bugly.init(getApplicationContext(), "c39b279c07", false);
    }

    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        // 此处cihang-db表示数据库名称 可以任意填写
        mHelper = new DaoMaster.DevOpenHelper(this, "cihang-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        UrlFilter.InitUrlFilter();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public List<ChinaIP> getChinaIPList(){return mAllIP;}

    public List<String> GetBlackList(){return BlackUrl;}

    public SQLiteDatabase getDb() {
        return db;
    }

    public class ChinaIP{//用于保存中国IP号段
        public long min;
        public long max;
    }

}
