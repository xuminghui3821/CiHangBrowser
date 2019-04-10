package org.fanhuang.cihangbrowser.activity.setting;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/14.
 */

public class SetDefaultBrowserActivity extends AppCompatActivity {
    @BindView(R.id.set)
    LinearLayout set;
    @BindView(R.id.setok)
    LinearLayout setok;
    @BindView(R.id.clean)
    LinearLayout clean;

    private static final int IS_DEFAULT = 0;//已经是默认浏览器了
    private static final int NO_SET_DEFAULT = 1;//还没有设置默认浏览器
    private static final int NO_DEFAULT = 2;//其他浏览器为默认浏览器
    private int state = NO_DEFAULT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_default_browser);
        ButterKnife.bind(this);
        Init();//判断当前手机的默认浏览器状态
        LayoutSet();//根据状态设置不同的界面
    }

    private void Init() {
        PackageManager pm = getBaseContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.google.com"));
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info.activityInfo.packageName.equals("android")) {
            state = NO_SET_DEFAULT;//没有设置
        } else if (info.activityInfo.packageName.equals("org.fanhuang.cihangbrowser")) {//已经是默认浏览器
            state = IS_DEFAULT;//已经是默认浏览器
        } else {//如果不是慈航浏览器
            state = NO_DEFAULT;//其他浏览器设置的默认
        }
    }

    @Override
    protected void onResume() {
        Init();//判断当前手机的默认浏览器状态
        LayoutSet();//根据状态设置不同的界面
        super.onResume();
    }

    private void LayoutSet() {
        if (state == IS_DEFAULT) {
            set.setVisibility(View.GONE);
            setok.setVisibility(View.VISIBLE);
            clean.setVisibility(View.GONE);
        } else if (state == NO_DEFAULT) {
            set.setVisibility(View.GONE);
            clean.setVisibility(View.VISIBLE);
            setok.setVisibility(View.GONE);
        } else {
            clean.setVisibility(View.GONE);
            set.setVisibility(View.VISIBLE);
            setok.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.clean_btn)
    public void OnCLickCleanBtn() {//点击清理按钮
        PackageManager pm = getBaseContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.google.com"));
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String pkgName = "package:" + info.activityInfo.packageName;
        Intent intent_to = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse(pkgName));
        startActivity(intent_to);
    }

    @OnClick(R.id.set_btn)
    public void OnClientSetBtn() {//设置默认浏览器
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html"));
        ComponentName name = new ComponentName("android", "com.android.internal.app.ResolverActivity");
        intent.setComponent(name);
        startActivity(intent);
    }
}
