package org.fanhuang.cihangbrowser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import org.fanhuang.cihangbrowser.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lan on 2017/4/15.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    private List<String> tag;
    private Context mContext;
    private int flag;

    public MainRecyclerViewAdapter(Context context, List<String> tag, int flag) {
        this.tag = tag;
        mContext = context;
        this.flag = flag;
    }

    @Override
    public MainRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainRecyclerViewAdapter.ViewHolder holder, int position) {
        if (flag == position) {
            holder.img.setBackgroundResource(R.mipmap.wds_dock_n);
        } else {
            holder.img.setBackgroundResource(R.mipmap.wds_dock_dis);
        }
    }

    @Override
    public int getItemCount() {
        return tag.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void chageview(int flag) {
        this.flag = flag;
        this.notifyDataSetChanged();
    }
}
