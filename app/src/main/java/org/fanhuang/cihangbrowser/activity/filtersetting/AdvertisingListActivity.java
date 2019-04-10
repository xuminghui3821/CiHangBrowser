package org.fanhuang.cihangbrowser.activity.filtersetting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.adapter.AdvertisingListAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.AdvertisingUrl;
import org.fanhuang.cihangbrowser.entity.UserBlackUrl;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.AdvertisingUrlDao;
import org.fanhuang.cihangbrowser.gen.UserBlackUrlDao;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2019/1/19.
 */

public class AdvertisingListActivity extends AppCompatActivity implements AdvertisingListAdapter.OnItemOclickListers {
    @BindView(R.id.title_one)
    LinearLayout title_one;
    @BindView(R.id.title_two)
    LinearLayout title_two;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.add)
    TextView add;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.empty)
    RelativeLayout empty;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    AdvertisingUrlDao mAdvertisingUrlDao = null;
    private LinearLayoutManager layoutManager;
    private AdvertisingListAdapter bdvertisingListAdapter;
    private List<AdvertisingUrl> AdvertisingList;//记录列表个数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising_list);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mAdvertisingUrlDao = MyAppAction.getInstances().getDaoSession().getAdvertisingUrlDao();
        AdvertisingList = mAdvertisingUrlDao.queryBuilder().orderAsc(AdvertisingUrlDao.Properties.Id).list();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        bdvertisingListAdapter = new AdvertisingListAdapter(AdvertisingListActivity.this, AdvertisingList);
        recyclerView.setAdapter(bdvertisingListAdapter);
        if (AdvertisingList.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//是退出按钮
            if (bdvertisingListAdapter.checkMode == true) {
                title_one.setVisibility(View.VISIBLE);
                title_two.setVisibility(View.GONE);
                bdvertisingListAdapter.initCheck(false);//将所有的选项全都清空
                bdvertisingListAdapter.chageView(AdvertisingList, false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick(R.id.add)
    public void addCLick() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_url_change, null);
        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setView(view).create();
        mDialog.setCancelable(true);
        final EditText url = (EditText) view.findViewById(R.id.url);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(url.getText())) {
                    CustomToast.toast("请输入正确的链接");
                    return;
                }
                String newurl = url.getText().toString().trim();
                AdvertisingUrlDao mAdvertisingUrlDao = MyAppAction.getInstances().getDaoSession().getAdvertisingUrlDao();
                AdvertisingUrl entity = mAdvertisingUrlDao.queryBuilder().where(AdvertisingUrlDao.Properties.Url.eq(newurl)).unique();// 查找是否存在相同数据
                if (entity == null) {
                    AdvertisingUrl mAdvertisingUrl = new AdvertisingUrl();
                    mAdvertisingUrl.setUrl(newurl);
                    mAdvertisingUrlDao.insert(mAdvertisingUrl);
                    AdvertisingList = mAdvertisingUrlDao.queryBuilder().orderAsc(AdvertisingUrlDao.Properties.Id).list();//获取最新列表状态
                    bdvertisingListAdapter.chageView(AdvertisingList, false);//更新列表
                    CustomToast.toast("添加成功");
                } else
                    CustomToast.toast("存在相同链接");
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

    @OnClick(R.id.back)
    public void backClick() {
        this.finish();
    }

    @OnClick(R.id.cancel)
    public void cancelClick() {
        title_one.setVisibility(View.VISIBLE);
        title_two.setVisibility(View.GONE);
        bdvertisingListAdapter.initCheck(false);//将所有的选项全都清空
        bdvertisingListAdapter.chageView(AdvertisingList, false);
    }

    @OnClick(R.id.delete)
    public void DeleteClick() {
        boolean isDelete = false;
        Map<Integer, Boolean> isCheck_delete = bdvertisingListAdapter.isCheck;
        for (int i = 0; i < AdvertisingList.size(); i++) {
            if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                isCheck_delete.remove(i);
                mAdvertisingUrlDao.delete(AdvertisingList.get(i));
                isDelete = true;
            }
        }
        if (isDelete) {
            AdvertisingList = mAdvertisingUrlDao.queryBuilder().orderAsc(AdvertisingUrlDao.Properties.Id).list();
            CustomToast.toast("删除成功");
        }
        title_one.setVisibility(View.VISIBLE);
        title_two.setVisibility(View.GONE);
        bdvertisingListAdapter.chageView(AdvertisingList, false);
    }

    @Override
    public void onItemClick(boolean checkMode) {
        if (checkMode == true) {
            title_one.setVisibility(View.GONE);
            title_two.setVisibility(View.VISIBLE);
            bdvertisingListAdapter.chageView(AdvertisingList, true);
        }
    }
}

