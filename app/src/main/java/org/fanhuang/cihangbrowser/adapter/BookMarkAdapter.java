package org.fanhuang.cihangbrowser.adapter;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.BrowserMainActivity;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.BookmarkEntity;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.Utlis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaohuihui on 2018/12/5.
 */

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder> {
    public Activity mContent;
    public Fragment mFragment;
    private List<BookmarkEntity> entities;
    private MaterialRippleLayout mTextView;
    private OnItemLongClick OnItemLongClick;//存放的由Activity返回的回调接口，方便fragment和Activity之间传递数据
    public void setIsCheck(Map<Integer, Boolean> isCheck) {
        this.isCheck = isCheck;
    }

    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    private boolean tag;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    private int action = 0;


    public BookMarkAdapter(Activity context, List<BookmarkEntity> bookmarkEntities, TextView textView) {
        this.mContent = context;
        this.entities = bookmarkEntities;
        // this.mTextView = textView;
        initCheck(false);
    }

    public BookMarkAdapter(Fragment fragment,Activity context, List<BookmarkEntity> bookmarkEntities, MaterialRippleLayout textView) {
        this.mFragment = fragment;
        this.mContent = context;
        this.entities = bookmarkEntities;
        this.mTextView = textView;
        initCheck(false);
        if (fragment instanceof OnItemLongClick) {
            OnItemLongClick = (OnItemLongClick) fragment;
        }
    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < entities.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }


    @Override
    public BookMarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.item_book_mark, parent, false);
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookMarkViewHolder holder, final int position) {
        final BookmarkEntity item = entities.get(position);
        if (tag) {
            holder.edit.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
        } else {
            holder.edit.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
        }
        if (item.getIco() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(item.getIco(), 0, item.getIco().length);
            holder.icon.setImageBitmap(bitmap);
        }
        holder.title.setText(item.getTitle());
        holder.url.setText(item.getUrl());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck.put(position, b);
                showText();
            }
        });

        // 设置状态
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        holder.checkbox.setChecked(isCheck.get(position));
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(mContent).inflate(R.layout.edit_bookmark_dialog, null);
                final android.support.v7.app.AlertDialog mDialog = new android.support.v7.app.AlertDialog.Builder(mContent)
                        .setView(view).create();
                mDialog.setCancelable(true);
                final EditText title = (EditText) view.findViewById(R.id.title);
                final EditText url = (EditText) view.findViewById(R.id.url);
                title.setText(item.getTitle());
                url.setText(item.getUrl());
                view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(title.getText())) {
                            CustomToast.toast("请输入正确的名称");
                            return;
                        }
                        String txt = url.getText().toString().trim();
                        if (Patterns.WEB_URL.matcher(txt).matches()) {
                            BookmarkEntityDao bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
                            BookmarkEntity histories = bookmarkEntityDao.queryBuilder().where(BookmarkEntityDao.Properties.Id.eq(item.getId())).unique();//获取该item所对应的数据
                            BookmarkEntity entity = new BookmarkEntity();
                            entity.setTime(Utlis.getTime());
                            entity.setTitle(title.getText().toString().trim());
                            entity.setUrl(url.getText().toString().trim());
                            entity.setId(item.getId());
                            entity.setIco(histories.getIco());
                            bookmarkEntityDao.update(entity);
                            entities = bookmarkEntityDao.queryBuilder().orderDesc(BookmarkEntityDao.Properties.Id).list();
                            chageView(entities,tag);
                            CustomToast.toast("修改成功");
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
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action != 0) {
                    setSelectItem(position);
                } else {
                    Intent intent = new Intent(mContent, BrowserMainActivity.class);
                    intent.putExtra("url", item.getUrl());
                    mContent.startActivity(intent);
                }
                showText();
            }
        });
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (action != 0) {
                    setSelectItem(position);
                } else {
                    //cartDailog(position);
                    OnItemLongClick.onLongItemClick();
                }
                showText();
                return true;
            }
        });
    }
    public interface OnItemLongClick {
        void onLongItemClick();
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }



    public void chageView(List<BookmarkEntity> bookmarkEntities, boolean tag) {
        this.entities = bookmarkEntities;
        this.tag = tag;
        showText();
        this.notifyDataSetChanged();
    }


    public void checkAll(List<BookmarkEntity> bookmarkEntities, boolean all) {
        this.entities = bookmarkEntities;
        if (all) {
            initCheck(true);
        } else {
            initCheck(false);
        }
        showText();
        this.notifyDataSetChanged();
    }

    public void setSelectItem(int position) {
        //对当前状态取反
        if (isCheck.get(position)) {
            isCheck.put(position, false);
        } else {
            isCheck.put(position, true);
        }
        notifyItemChanged(position);
    }

    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    private void showText() {
        if (removeHister()) {
            mTextView.setEnabled(true);
        } else {
            mTextView.setEnabled(false);
        }
    }

    //删除所有
    private boolean removeHister() {
        Map<Integer, Boolean> isCheck_delete = getMap();
        for (int i = 0; i < entities.size(); i++) {
            if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                return true;
            }
        }
        return false;
    }

    //    private void cartDailog(final int position) {
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContent);
//        final BookmarkEntity item = entities.get(position);
//        final View views = LayoutInflater.from(mContent).inflate(R.layout.view_book, null);
//        bottomSheetDialog.contentView(views);
//        bottomSheetDialog.inDuration(200);
//        bottomSheetDialog.outDuration(200);
//        bottomSheetDialog.cancelable(true);
//        bottomSheetDialog.show();
//        TextView open = (TextView) views.findViewById(R.id.open);
//        open.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContent, BrowserMainActivity.class);
//                intent.putExtra("url", item.getUrl());
//                mContent.startActivity(intent);
//                bottomSheetDialog.dismiss();
//            }
//        });
//        TextView copy = (TextView) views.findViewById(R.id.copy);
//        copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClipboardManager cmb = (ClipboardManager) mContent.getSystemService(Context.CLIPBOARD_SERVICE);
//                cmb.setText(item.getUrl());
//                bottomSheetDialog.dismiss();
//                CustomToast.toast("已复制，长按输入框可粘贴");
//            }
//        });
//        TextView delete = (TextView) views.findViewById(R.id.delete);
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    BookmarkEntityDao bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
//                    bookmarkEntityDao.deleteByKey(item.getId());
//                    entities.remove(position);
//                    showText();
//                    notifyDataSetChanged();
//                    CustomToast.toast("删除成功");
//                } catch (Exception e) {
//                    CustomToast.toast("删除失败");
//                }
//                bottomSheetDialog.dismiss();
//            }
//        });
//    }

    class BookMarkViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.url)
        TextView url;
        @BindView(R.id.edit)
        LinearLayout edit;
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.lCheckbox)
        RelativeLayout lCheckbox;
        @BindView(R.id.item)
        LinearLayout item;

        public BookMarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
