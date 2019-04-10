package org.fanhuang.cihangbrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.entity.History;
import org.fanhuang.cihangbrowser.java_bean.HistoryBean;
import org.fanhuang.cihangbrowser.utils.Utlis;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaohuihui on 2018/12/6.
 */


public class HistoryAdapter extends BaseExpandableListAdapter {

    private List<HistoryBean> historyBeanList;
    private Context mContext;
    private OnChliodItemLister onChliodItemLister;


    public void setOnChliodItemLister(OnChliodItemLister onChliodItemLister) {
        this.onChliodItemLister = onChliodItemLister;
    }

    public HistoryAdapter(Context context, List<HistoryBean> historyBeen) {
        this.mContext = context;
        this.historyBeanList = historyBeen;

    }

    @Override
    public int getGroupCount() {
        return historyBeanList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return historyBeanList.get(groupPosition).getHistories().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return historyBeanList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return historyBeanList.get(groupPosition).getHistories().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history_father, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.father);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
        String time = historyBeanList.get(groupPosition).getTime();
        int day = Utlis.getDay(time);
        if (day == 0) {
            //  textView.setText("今天");
            textView.setText("今天");
        } else if (day == 1) {
            textView.setText("昨天");
        } else if (day == 2) {
            textView.setText("前天");
        } else {
            textView.setText(Utlis.getDataTime(time));
        }
        if (isExpanded) {
            imageView.setBackgroundResource(R.mipmap.history_time_on);
        } else {
            imageView.setBackgroundResource(R.mipmap.history_time_off);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final History item = historyBeanList.get(groupPosition).getHistories().get(childPosition);
        HistoryAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history_child, parent, false);
            holder = new HistoryAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HistoryAdapter.ViewHolder) convertView.getTag();
        }
        if (item.getIco() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(item.getIco(), 0, item.getIco().length);
            holder.icon.setImageBitmap(bitmap);
        }
        holder.title.setText(item.getTitle());
        holder.url.setText(item.getUrl());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChliodItemLister != null) {
                    onChliodItemLister.onClicks(groupPosition, childPosition);
                }
            }
        });
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onChliodItemLister != null) {
                    onChliodItemLister.onLongClick(groupPosition, childPosition);
                }
                return true;
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.url)
        TextView url;
        @BindView(R.id.item)
        LinearLayout item;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnChliodItemLister {
        void onClicks(int groupPosition, int childPosition);

        void onLongClick(int groupPosition, int childPosition);
    }

    public void chageView(List<HistoryBean> historyBeen) {
        this.historyBeanList = historyBeen;
        this.notifyDataSetChanged();
    }
}
