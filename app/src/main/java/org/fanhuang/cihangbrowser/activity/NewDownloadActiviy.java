package org.fanhuang.cihangbrowser.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.fragment.DownloadFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class NewDownloadActiviy extends SupportActivity {

    @BindView(R.id.back)
    MaterialRippleLayout back;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private String[] titles = {"下载管理"};
    //    private String[] titles = {"下载管理", "下载热榜"};
    private DownloadAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_download_activiy);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.back)
    public void back(View view) {
        this.finish();
    }

    private void init() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));//添加tab选项卡
        }

        pagerAdapter = new DownloadAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);//给Tabs设置适配器
    }

    private class DownloadAdapter extends FragmentStatePagerAdapter {
        private List<SupportFragment> fragments;

        public DownloadAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(DownloadFragment.newInstance());              //下载管理
//            fragments.add(DownloadHotFragment.newInstance());        //下载热榜
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
