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
import org.fanhuang.cihangbrowser.adapter.WhiteListAdapter;
import org.fanhuang.cihangbrowser.adapter.WhiteListAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.UserWhiteUrl;
import org.fanhuang.cihangbrowser.entity.UserWhiteUrl;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.UserWhiteUrlDao;
import org.fanhuang.cihangbrowser.gen.UserWhiteUrlDao;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/25.
 */

public class WhiteListActivity extends AppCompatActivity implements WhiteListAdapter.OnItemOclickListers {
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

    UserWhiteUrlDao userWhiteUrlDao = null;
    private LinearLayoutManager layoutManager;
    private WhiteListAdapter whiteListAdapter;
    private List<UserWhiteUrl> WhiteList;//记录列表个数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        userWhiteUrlDao = MyAppAction.getInstances().getDaoSession().getUserWhiteUrlDao();
        WhiteList = userWhiteUrlDao.queryBuilder().orderAsc(UserWhiteUrlDao.Properties.Url).list();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        whiteListAdapter = new WhiteListAdapter(WhiteListActivity.this, WhiteList);
        recyclerView.setAdapter(whiteListAdapter);
        if (WhiteList.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//是退出按钮
            if(whiteListAdapter.checkMode == true){
                title_one.setVisibility(View.VISIBLE);
                title_two.setVisibility(View.GONE);
                whiteListAdapter.initCheck(false);//将所有的选项全都清空
                whiteListAdapter.chageView(WhiteList, false);
                return  true;
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
                if (Patterns.WEB_URL.matcher(newurl).matches()) {
                    newurl = UrlFilter.HostDeleteHead(newurl);
                    UserWhiteUrlDao userWhiteUrlDao = MyAppAction.getInstances().getDaoSession().getUserWhiteUrlDao();
                    UserWhiteUrl userWhiteUrl = new UserWhiteUrl();
                    userWhiteUrl.setUrl(newurl);
                    userWhiteUrlDao.insert(userWhiteUrl);
                    WhiteList = userWhiteUrlDao.queryBuilder().orderAsc(UserWhiteUrlDao.Properties.Id).list();//获取最新列表状态
                    whiteListAdapter.chageView(WhiteList, false);//更新列表
                    CustomToast.toast("添加成功");
                } else {
                    CustomToast.toast("请输入正确的网址");
                }
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
        whiteListAdapter.initCheck(false);//将所有的选项全都清空
        whiteListAdapter.chageView(WhiteList, false);
    }

    @OnClick(R.id.delete)
    public void DeleteClick() {
        boolean isDelete = false;
        Map<Integer, Boolean> isCheck_delete = whiteListAdapter.isCheck;
        for (int i = 0; i < WhiteList.size(); i++) {
            if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                isCheck_delete.remove(i);
                userWhiteUrlDao.deleteByKey(WhiteList.get(i).getId());
                isDelete = true;
            }
        }
        if (isDelete) {
            WhiteList = userWhiteUrlDao.queryBuilder().orderAsc(UserWhiteUrlDao.Properties.Id).list();
            CustomToast.toast("删除成功");
        }
        title_one.setVisibility(View.VISIBLE);
        title_two.setVisibility(View.GONE);
        whiteListAdapter.chageView(WhiteList, false);
    }

    @Override
    public void onItemClick(boolean checkMode) {
        if (checkMode == true) {
            title_one.setVisibility(View.GONE);
            title_two.setVisibility(View.VISIBLE);
            whiteListAdapter.chageView(WhiteList, true);
        }
    }
}
