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
import org.fanhuang.cihangbrowser.adapter.KeyWordListAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.KeyWordList;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.KeyWordListDao;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/12/27.
 */

public class KeyListActivity extends AppCompatActivity implements KeyWordListAdapter.OnItemOclickListers {
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

    KeyWordListDao keyWordListDao = null;
    private LinearLayoutManager layoutManager;
    private KeyWordListAdapter keyWordListAdapter;
    private List<KeyWordList> keyWordLists;//记录列表个数

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_word_list);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        keyWordListDao = MyAppAction.getInstances().getDaoSession().getKeyWordListDao();
        keyWordLists = keyWordListDao.queryBuilder().orderAsc(KeyWordListDao.Properties.Keyword).list();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        keyWordListAdapter = new KeyWordListAdapter(KeyListActivity.this, keyWordLists);
        recyclerView.setAdapter(keyWordListAdapter);
        if (keyWordLists.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//是退出按钮
            if (keyWordListAdapter.checkMode == true) {
                title_one.setVisibility(View.VISIBLE);
                title_two.setVisibility(View.GONE);
                keyWordListAdapter.initCheck(false);//将所有的选项全都清空
                keyWordListAdapter.chageView(keyWordLists, false);
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
        final EditText e_word = (EditText) view.findViewById(R.id.url);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(e_word.getText())) {
                    CustomToast.toast("请输入正确的关键字");
                    return;
                }
                String keyword = e_word.getText().toString().trim();
                KeyWordListDao keyWordListDao = MyAppAction.getInstances().getDaoSession().getKeyWordListDao();
                KeyWordList keyWordList = new KeyWordList();
                keyWordList.setKeyword(keyword);
                keyWordListDao.insert(keyWordList);
                keyWordLists = keyWordListDao.queryBuilder().orderAsc(KeyWordListDao.Properties.Id).list();//获取最新列表状态
                MyAppAction.getInstances().keyWordHash.add(keyword,0);//将关键字添加到hash中
                keyWordListAdapter.chageView(keyWordLists, false);//更新列表
                CustomToast.toast("添加成功");
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
        keyWordListAdapter.initCheck(false);//将所有的选项全都清空
        keyWordListAdapter.chageView(keyWordLists, false);
    }

    @OnClick(R.id.delete)
    public void DeleteClick() {
        boolean isDelete = false;
        Map<Integer, Boolean> isCheck_delete = keyWordListAdapter.isCheck;
        for (int i = 0; i < keyWordLists.size(); i++) {
            if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                isCheck_delete.remove(i);
                keyWordListDao.delete(keyWordLists.get(i));
                MyAppAction.getInstances().keyWordHash.delete(keyWordLists.get(i).getKeyword());
                isDelete = true;
            }
        }
        if (isDelete) {
            keyWordLists = keyWordListDao.queryBuilder().orderAsc(KeyWordListDao.Properties.Id).list();
            CustomToast.toast("删除成功");
        }
        title_one.setVisibility(View.VISIBLE);
        title_two.setVisibility(View.GONE);
        keyWordListAdapter.chageView(keyWordLists, false);
    }

    @Override
    public void onItemClick(boolean checkMode) {
        if (checkMode == true) {
            title_one.setVisibility(View.GONE);
            title_two.setVisibility(View.VISIBLE);
            keyWordListAdapter.chageView(keyWordLists, true);
        }
    }
}
