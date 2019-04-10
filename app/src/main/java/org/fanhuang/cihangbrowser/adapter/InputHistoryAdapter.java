package org.fanhuang.cihangbrowser.adapter;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.entity.InputHistory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lan on 2017/4/11.
 */

public class InputHistoryAdapter extends BaseAdapter {
    private List<InputHistory> inputHistories;
    private Context mContext;
    private OnDeleteItem OnItemClick;//存放的由Activity返回的回调接口
    public InputHistoryAdapter(Context context, List<InputHistory> inputHistories) {
        this.inputHistories = inputHistories;
        this.mContext = context;
        if (mContext instanceof OnDeleteItem) {
            OnItemClick = (OnDeleteItem) mContext;
        }
    }

    @Override
    public int getCount() {
        return inputHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return inputHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_inputhistory, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Long Id = inputHistories.get(position).getId();
        viewHolder.name.setText(inputHistories.get(position).getName());
        if (Patterns.WEB_URL.matcher(viewHolder.name.getText().toString().trim()).matches()) {
            viewHolder.img.setBackgroundResource(R.mipmap.suggestion_history);
        } else {
            viewHolder.img.setBackgroundResource(R.mipmap.suggestion_search);
        }
        viewHolder.clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnItemClick.onItemClick(Id);
            }
        });
        return convertView;
    }

    public interface OnDeleteItem {
        void onItemClick(Long url);
    }

    static class ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.clean)
        LinearLayout clean;
        @BindView(R.id.relative)
        LinearLayout relative;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void chageview(List<InputHistory> inputHistories) {
        this.inputHistories = inputHistories;
        this.notifyDataSetChanged();
    }
}
