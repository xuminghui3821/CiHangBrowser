package org.fanhuang.cihangbrowser.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.CompoundButton;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.setting.SettingMainActivity;
import org.fanhuang.cihangbrowser.adapter.InputHistoryAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.gen.InputHistoryDao;
import org.fanhuang.cihangbrowser.entity.InputHistory;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaohuihui on 2018/11/14.
 */

public class SearchActivity extends AppCompatActivity implements InputHistoryAdapter.OnDeleteItem{
    @BindView(R.id.text_context)
    EditText textContext;
    @BindView(R.id.activity_search)
    LinearLayout activitySearch;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.pop)
    LinearLayout pop;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.sousuologo)
    ImageView sousuologo;
    private InputHistoryDao inputHistoryDao;
    private List<InputHistory> histories = new ArrayList<>();
    private InputHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        inputHistoryDao = MyAppAction.getInstances().getDaoSession().getInputHistoryDao();
        //从数据库中取出list
        histories = inputHistoryDao.queryBuilder().orderDesc(InputHistoryDao.Properties.Id).list();
        adapter = new InputHistoryAdapter(this, histories);
        listView.setAdapter(adapter);
        textContext.setText(getIntent().getStringExtra("url"));
        logo.setText("取消");
        textContext.setSelection(textContext.getText().length());
        textContext.selectAll();
        textContext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = textContext.getText().toString().trim();
                if (TextUtils.isEmpty(txt)) {
                    histories = inputHistoryDao.queryBuilder().orderDesc(InputHistoryDao.Properties.Id).list();
                    adapter.chageview(histories);
                } else {
                    histories = inputHistoryDao.queryBuilder().where(InputHistoryDao.Properties.Name.like("%" + txt + "%")).list();
                    adapter.chageview(histories);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Patterns.WEB_URL.matcher(s).matches()) {        //创建Patterns对象,并对s进行正则匹配
                    logo.setText("进入");
                } else {
                    logo.setText("搜索");
                }
                if (TextUtils.isEmpty(s)) {
                    logo.setText("取消");
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = histories.get(position).getName();
                String strUrl;
                if (Patterns.WEB_URL.matcher(url).matches()) {
                    if (url.contains("http")) {
                        strUrl = url;
                    } else {
                        strUrl = "http://" + url;
                    }
                } else {
                    strUrl =  String.valueOf( SharedPreferencesUtils.get(SearchActivity.this,
                            "search","https://cn.bing.com/search?q=")) + url;//设置搜索链接;//必应的搜索链接
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textContext.getWindowToken(), 0);
                Intent intent = new Intent();
                intent.putExtra("url", strUrl);
                setResult(5566, intent);
                SearchActivity.this.finish();
            }
        });
        String strUrl = String.valueOf( SharedPreferencesUtils.get(SearchActivity.this,
                "search","https://cn.bing.com/search?q="));
        if(strUrl.indexOf("baidu") != -1)
            sousuologo.setImageResource(R.mipmap.baidu);
        else if(strUrl.indexOf("bing") != -1)
            sousuologo.setImageResource(R.mipmap.bing);
        else
            sousuologo.setImageResource(R.mipmap.menu);
    }

@OnClick(R.id.setSearch)
public void SetSearch(){
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
    com.rey.material.widget.ListView listView = (com.rey.material.widget.ListView) views.findViewById(R.id.listView);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0: {//必应
                    bottomSheetDialog.dismiss();
                    SharedPreferencesUtils.put(SearchActivity.this,"search","https://cn.bing.com/search?q=");//设置搜索链接
                    sousuologo.setImageResource(R.mipmap.bing);
                    CustomToast.toast("设置成功");
                    break;
                }
                case 1: {//百度
                    bottomSheetDialog.dismiss();
                    SharedPreferencesUtils.put(SearchActivity.this,"search","https://m.baidu.com/s?word=");//设置搜索链接
                    sousuologo.setImageResource(R.mipmap.baidu);
                    CustomToast.toast("设置成功");
                    break;
                }
                case 2: {//慈航
                    bottomSheetDialog.dismiss();
                    SharedPreferencesUtils.put(SearchActivity.this,"search","http://www.fhzd.org/cms/wenku/index.php/Search-Index-index.html?keyword=");//设置搜索链接
                    sousuologo.setImageResource(R.mipmap.menu);
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

    @OnClick(R.id.logo)
    public void logo(View view) {

        if (TextUtils.isEmpty(textContext.getText().toString().trim())) {
            if (logo.getText().equals("取消")) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textContext.getWindowToken(), 0);
                this.finish();
            } else {
                CustomToast.toast("内容不能为空");
            }
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textContext.getWindowToken(), 0);
        String strUrl;
        if (logo.getText().equals("取消")) {
            this.finish();
        } else if (logo.getText().equals("搜索")) {
            strUrl = String.valueOf( SharedPreferencesUtils.get(SearchActivity.this,
                    "search","https://cn.bing.com/search?q=")) + textContext.getText().toString().trim();
            Intent intent = new Intent();
            intent.putExtra("url", strUrl);
            setResult(RESULT_OK, intent);
            addInputHistory();
            SearchActivity.this.finish();
        } else if (logo.getText().equals("进入")) {
            if (textContext.getText().toString().trim().contains("http")) {
                strUrl = textContext.getText().toString().trim();
            } else {
                strUrl = "http://" + textContext.getText().toString().trim();
            }
            Intent intent = new Intent();
            intent.putExtra("url", strUrl);
            setResult(RESULT_OK, intent);
            addInputHistory();
            SearchActivity.this.finish();
        }
    }

    private void addInputHistory() {  //添加历史记录
        String input = textContext.getText().toString().trim();
        InputHistory history = new InputHistory();
        history.setName(textContext.getText().toString().trim());
        List<InputHistory> histories = inputHistoryDao.queryBuilder().orderAsc(InputHistoryDao.Properties.Id).list();
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).getName().equals(input)) {
                inputHistoryDao.deleteByKey(histories.get(i).getId());//删除已有的数据
            }
        }
        inputHistoryDao.insert(history);
    }

    @OnClick(R.id.activity_search)  //关闭输入法
    public void activity_search(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textContext.getWindowToken(), 0);
       // this.finish();
    }

    @Override
    public void onItemClick(Long id) {//在这里删除数据中对应的数据
        inputHistoryDao.deleteByKey(id);//删除指定数据
        histories = inputHistoryDao.queryBuilder().orderDesc(InputHistoryDao.Properties.Id).list();
        adapter.chageview(histories);
    }
    class MySetSearch extends BaseAdapter {
        private String[] Searchsize = {"必应搜索","百度搜索","慈航文库"};
        private String url = "";
        private int type;

        public MySetSearch() {
            url = String.valueOf(SharedPreferencesUtils.get(SearchActivity.this, "search", "https://cn.bing.com/search?q="));
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
            SearchActivity.MySetSearch.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_font_size, parent, false);
                holder = new SearchActivity.MySetSearch.ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SearchActivity.MySetSearch.ViewHolder) convertView.getTag();
            }
            holder.font.setText(Searchsize[position]);
            if (position == type) {
                holder.font.setTextColor(SearchActivity.this.getResources().getColor(R.color.base_color));
                holder.icon.setVisibility(View.VISIBLE);
            } else {
                holder.font.setTextColor(SearchActivity.this.getResources().getColor(R.color.black));
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
}
