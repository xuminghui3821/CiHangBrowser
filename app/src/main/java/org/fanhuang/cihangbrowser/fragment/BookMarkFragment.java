package org.fanhuang.cihangbrowser.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.adapter.BookMarkAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.BookmarkEntity;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.Utlis;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by xiaohuihui on 2018/12/5.
 */

public class BookMarkFragment extends SupportFragment implements BookMarkAdapter.OnItemLongClick{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    MaterialRippleLayout add;
    @BindView(R.id.edit)
    MaterialRippleLayout edit;
    @BindView(R.id.l2)
    LinearLayout l2;
    @BindView(R.id.deleteText)
    TextView deleteText;
    @BindView(R.id.l1)
    FrameLayout l1;
    @BindView(R.id.r1)
    RelativeLayout r1;
    @BindView(R.id.text)
    RelativeLayout text;
    @BindView(R.id.textEdit)
    TextView textEdit;
    @BindView(R.id.delete)
    MaterialRippleLayout delete;

    private String mParam1;
    private String mParam2;
    private LinearLayoutManager layoutManager;
    private OnFragmentInteractionListener mListener;
    private List<BookmarkEntity> entities;//记录列表个数
    private BookmarkEntityDao bookmarkEntityDao;
    public BookMarkAdapter markAdapter;

    public BookMarkFragment() {
        // Required empty public constructor
    }

    public static BookMarkFragment newInstance(String param1, String param2) {
        BookMarkFragment fragment = new BookMarkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookMarkFragment newInstance() {
        BookMarkFragment fragment = new BookMarkFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_mark, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void init() {
        bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        entities = bookmarkEntityDao.queryBuilder().orderDesc(BookmarkEntityDao.Properties.Id).list();

        if (entities.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            text.setVisibility(View.INVISIBLE);
            edit.setEnabled(true);
            textEdit.setEnabled(true);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            text.setVisibility(View.VISIBLE);
            edit.setEnabled(false);
            textEdit.setEnabled(false);
        }
        markAdapter = new BookMarkAdapter(this,getActivity(), entities, delete);
        recyclerView.setAdapter(markAdapter);
        delete.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.VISIBLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLongItemClick() {
        if (markAdapter.getAction() == 0) {
            markAdapter.chageView(entities, true);
            markAdapter.setAction(1);
            delete.setVisibility(View.VISIBLE);
            l2.setVisibility(View.INVISIBLE);
            onButtonPressed(1);
        } else {
            markAdapter.chageView(entities, false);
            markAdapter.setAction(0);
            delete.setVisibility(View.INVISIBLE);
            l2.setVisibility(View.VISIBLE);
            onButtonPressed(0);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int uri);
    }

    @OnClick(R.id.edit)
    public void edit() {
        if (markAdapter.getAction() == 0) {
            markAdapter.chageView(entities, true);
            markAdapter.setAction(1);
            delete.setVisibility(View.VISIBLE);
            l2.setVisibility(View.INVISIBLE);
            onButtonPressed(1);
        } else {
            markAdapter.chageView(entities, false);
            markAdapter.setAction(0);
            delete.setVisibility(View.INVISIBLE);
            l2.setVisibility(View.VISIBLE);
            onButtonPressed(0);
        }
    }

    @OnClick(R.id.delete)
    public void delete(View view) {
        boolean isDelete = false;
        List<BookmarkEntity> deleteList = new ArrayList<>();
        Map<Integer, Boolean> isCheck_delete = markAdapter.getMap();
        for (int i = 0; i < entities.size(); i++) {
            if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                isCheck_delete.remove(i);
                bookmarkEntityDao.deleteByKey(entities.get(i).getId());
                deleteList.add(entities.get(i));
                isDelete = true;
            }
        }
        if (isDelete) {
            entities = bookmarkEntityDao.queryBuilder().orderDesc(BookmarkEntityDao.Properties.Id).list();
            markAdapter.setIsCheck(isCheck_delete);
            CustomToast.toast("删除成功");
        }
        markAdapter.chageView(entities, true);
    }


    public void setAall(boolean bl) {
        markAdapter.checkAll(entities, bl);
    }

    @OnClick(R.id.add)
    public void add() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_bookmark_dialog, null);
        final android.support.v7.app.AlertDialog mDialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(view).create();
        mDialog.setCancelable(true);

        final EditText editText = (EditText) view.findViewById(R.id.e_tv);
        final EditText title = (EditText) view.findViewById(R.id.title);
        final EditText url = (EditText) view.findViewById(R.id.url);

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookmarkEntityDao bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
                BookmarkEntity entity = new BookmarkEntity();
                entity.setTime(Utlis.getTime());
                if (TextUtils.isEmpty(title.getText())) {
                    CustomToast.toast("请输入正确的名称");
                    return;
                }
                String txt = url.getText().toString().trim();
                if (Patterns.WEB_URL.matcher(txt).matches()) {    //txt是否匹配网址格式
                    entity.setTitle(title.getText().toString().trim());
                    entity.setUrl(url.getText().toString().trim());
                    BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.mipmap.suggestion_history);
                    entity.setIco(Utlis.GetBitmapByte(bd.getBitmap()));
                    bookmarkEntityDao.insert(entity);
                    entities = bookmarkEntityDao.queryBuilder().orderDesc(BookmarkEntityDao.Properties.Id).list();
                    markAdapter.chageView(entities, false);
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
}