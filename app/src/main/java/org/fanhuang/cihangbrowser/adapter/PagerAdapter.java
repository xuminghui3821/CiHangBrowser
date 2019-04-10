package org.fanhuang.cihangbrowser.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.interfaces.ShowWayListerCallBack;

import java.util.List;


public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.PageViewHolder> {
    private Context mContenxt;
    private ShowWayListerCallBack mCallBack;

    public ShowWayListerCallBack getmCallBack() {
        return mCallBack;
    }

    public void setmCallBack(ShowWayListerCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }


    private List<Bitmap> logos;
    private List<Bitmap> bitmaps;
    private List<String> title;
    private List<String> address;
    private int mPosition;

    public PagerAdapter(Context context,List<Bitmap>logos, List<Bitmap> bitmaps, List<String> titles, List<String> address, int position) {
        this.logos = logos;
        this.mContenxt = context;
        this.bitmaps = bitmaps;
        this.title = titles;
        this.address = address;
        this.mPosition = position;
    }

    @Override
    public PagerAdapter.PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PageViewHolder(LayoutInflater.from(mContenxt).inflate(R.layout.item_pager, parent, false));
    }

    @Override
    public void onBindViewHolder(PagerAdapter.PageViewHolder holder, final int position) {
        holder.imageView.setImageBitmap(bitmaps.get(position));
        holder.logo.setImageBitmap(logos.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallBack != null) {
                    mCallBack.showPager(1, position);
                }
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallBack != null) {
                    mCallBack.showPager(2, position);
                }
            }
        });
        holder.textView.setText(title.get(position));
        if (position == mPosition) {
            holder.textView.setTextColor(mContenxt.getResources().getColor(R.color.white));
            holder.main.setBackgroundColor(mContenxt.getResources().getColor(R.color.blueEndColor));
            holder.img.setBackgroundResource(R.mipmap.wds_close_n);
        } else {
            holder.textView.setTextColor(mContenxt.getResources().getColor(R.color.black));
            holder.main.setBackgroundColor(mContenxt.getResources().getColor(R.color.white));
            holder.img.setBackgroundResource(R.mipmap.wds_close);
        }
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        LinearLayout main;
        ImageView img;
        ImageView logo;

        public PageViewHolder(View itemView) {
            super(itemView);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.text);
            main = (LinearLayout) itemView.findViewById(R.id.r1);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

}
