package org.fanhuang.cihangbrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.AdvertisingUrl;
import org.fanhuang.cihangbrowser.entity.AdvertisingUrl;
import org.fanhuang.cihangbrowser.filter.UrlFilter;
import org.fanhuang.cihangbrowser.gen.AdvertisingUrlDao;
import org.fanhuang.cihangbrowser.gen.AdvertisingUrlDao;
import org.fanhuang.cihangbrowser.utils.CustomToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaohuihui on 2019/1/19.
 */

public class AdvertisingListAdapter extends RecyclerView.Adapter<AdvertisingListAdapter.AdvertisingListHolder> {

    public Context mContext;
    public List<AdvertisingUrl> entities;
    public boolean checkMode = false;//当前是否是选择模式
    private AdvertisingListAdapter.OnItemOclickListers OnItemLongClick;//存放的由Activity返回的回调接口
    public Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public AdvertisingListAdapter(Context Context, List<AdvertisingUrl> advertisingList1) {
        this.mContext = Context;
        this.entities = advertisingList1;
        checkMode = false;
        if (mContext instanceof AdvertisingListAdapter.OnItemOclickListers) {
            OnItemLongClick = (AdvertisingListAdapter.OnItemOclickListers) mContext;
        }
        initCheck(false);
    }

    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < entities.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    public void deleteCheck() {//删除选中的标签

    }

    public void chageView(List<AdvertisingUrl> bookmarkEntities, boolean ischeck) {
        this.checkMode = ischeck;
        this.entities = bookmarkEntities;
        this.notifyDataSetChanged();
    }


    @Override
    public AdvertisingListAdapter.AdvertisingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_advertising_list, parent, false);
        return new AdvertisingListAdapter.AdvertisingListHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdvertisingListAdapter.AdvertisingListHolder holder, final int position) {
        final AdvertisingUrl item = entities.get(position);
        if (checkMode) {
            holder.edit.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
        } else {
            holder.edit.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
        }
        holder.url.setText(item.getUrl());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck.put(position, b);
            }
        });
        // 设置状态
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        holder.checkbox.setChecked(isCheck.get(position));
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck.put(position, b);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_url_change, null);
                final android.support.v7.app.AlertDialog mDialog = new android.support.v7.app.AlertDialog.Builder(mContext)
                        .setView(view).create();
                mDialog.setCancelable(true);
                final EditText url = (EditText) view.findViewById(R.id.url);
                url.setText(item.getUrl());
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
                            item.setUrl(newurl);
                            mAdvertisingUrlDao.update(item);
                            entities = mAdvertisingUrlDao.queryBuilder().orderAsc(AdvertisingUrlDao.Properties.Id).list();//获取最新列表状态
                            chageView(entities, false);//更新列表
                            CustomToast.toast("修改成功");
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
        });


        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OnItemLongClick.onItemClick(true);
                return true;
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMode == true) {
                    holder.checkbox.setChecked(!holder.checkbox.isChecked());
                } else
                    holder.edit.performClick();
            }
        });
    }

    public interface OnItemOclickListers {
        void onItemClick(boolean IsCheck);
    }


    @Override
    public int getItemCount() {
        return entities.size();
    }

    class AdvertisingListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item)
        LinearLayout item;
        @BindView(R.id.url)
        TextView url;
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.edit)
        LinearLayout edit;

        public AdvertisingListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
