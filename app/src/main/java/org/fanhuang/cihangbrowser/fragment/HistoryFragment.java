package org.fanhuang.cihangbrowser.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.rey.material.app.BottomSheetDialog;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.BrowserMainActivity;
import org.fanhuang.cihangbrowser.adapter.HistoryAdapter;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.entity.BookmarkEntity;
import org.fanhuang.cihangbrowser.entity.History;
import org.fanhuang.cihangbrowser.gen.BookmarkEntityDao;
import org.fanhuang.cihangbrowser.gen.HistoryDao;
import org.fanhuang.cihangbrowser.java_bean.HistoryBean;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.Utlis;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class HistoryFragment extends SupportFragment implements HistoryAdapter.OnChliodItemLister {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.expanded_menu)
    ExpandableListView expandedMenu;
    @BindView(R.id.r1)
    RelativeLayout r1;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.deleteText)
    TextView deleteText;
    @BindView(R.id.delete)
    MaterialRippleLayout delete;
    @BindView(R.id.l1)
    LinearLayout l1;
    @BindView(R.id.no)
    RelativeLayout no;
    private List<Group> groups = new ArrayList<Group>();
    private String mParam1;
    private String mParam2;
    private HistoryDao historyDao;
    private boolean isALL;
    private List<HistoryBean> historyBeanList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private List<History> histories;
    private HistoryAdapter historyAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        historyDao = MyAppAction.getInstances().getDaoSession().getHistoryDao();
        histories = historyDao.queryBuilder().orderDesc(HistoryDao.Properties.Id).list();//倒序查询

        Map<Integer, Object> map = new TreeMap<>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                }
        );
        for (int i = 0; i < histories.size(); i++) {
            map.put(Utlis.getDay(histories.get(i).getTime()), histories.get(i));
        }
        Set table = map.keySet();
        Iterator it = table.iterator();
        while (it.hasNext()) {
            List<History> histories1 = new ArrayList<>();
            Object obj = it.next();
            HistoryBean bean = new HistoryBean();
            for (int j = 0; j < histories.size(); j++) {
                if (Utlis.getDay(histories.get(j).getTime()) == (Integer) obj) {
                    History history = histories.get(j);
                    histories1.add(history);
                    bean.setHistories(histories1);
                }
            }
            bean.setTime(histories1.get(0).getTime());
            historyBeanList.add(bean);
        }
        historyAdapter = new HistoryAdapter(getContext(), historyBeanList);
        expandedMenu.setAdapter(historyAdapter);
        historyAdapter.setOnChliodItemLister(this);
        for (int i = 0; i < historyAdapter.getGroupCount(); i++) {
            expandedMenu.expandGroup(i);
        }
        expandedMenu.setGroupIndicator(null);
        if (histories.size() > 0) {
            no.setVisibility(View.INVISIBLE);
            expandedMenu.setVisibility(View.VISIBLE);
            delete.setEnabled(true);
        } else {
            no.setVisibility(View.VISIBLE);
            expandedMenu.setVisibility(View.INVISIBLE);
            delete.setEnabled(false);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClicks(int groupPosition, int childPosition) {
        Intent intent = new Intent(getActivity(), BrowserMainActivity.class);
        intent.putExtra("url", historyBeanList.get(groupPosition).getHistories().get(childPosition).getUrl());
        getActivity().startActivity(intent);
    }

    @Override
    public void onLongClick(final int groupPosition, final int childPosition) {
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
//        final History item = historyBeanList.get(groupPosition).getHistories().get(childPosition);
//        final View views = LayoutInflater.from(getContext()).inflate(R.layout.view_history, null);
//        bottomSheetDialog.contentView(views);
//        bottomSheetDialog.inDuration(200);
//        bottomSheetDialog.outDuration(200);
//        bottomSheetDialog.cancelable(true);
//        bottomSheetDialog.show();
//        TextView open = (TextView) views.findViewById(R.id.open);
//        open.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), BrowserMainActivity.class);
//                intent.putExtra("url", item.getUrl());
//                getContext().startActivity(intent);
//                bottomSheetDialog.dismiss();
//            }
//        });
//        TextView copy = (TextView) views.findViewById(R.id.copy);
//        copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
//                    historyDao.deleteByKey(item.getId());
//                    historyBeanList.get(groupPosition).getHistories().remove(childPosition);
//                    CustomToast.toast("删除成功");
//                    historyAdapter.chageView(historyBeanList);
//                } catch (Exception e) {
//                    CustomToast.toast("删除失败");
//                }
//                bottomSheetDialog.dismiss();
//            }
//        });
//        TextView add = (TextView) views.findViewById(R.id.add);
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    BookmarkEntityDao bookmarkEntityDao = MyAppAction.getInstances().getDaoSession().getBookmarkEntityDao();
//                    BookmarkEntity entity = new BookmarkEntity();
//                    entity.setTime(Utlis.getTime());
//                    entity.setTitle(item.getTitle());
//                    entity.setUrl(item.getUrl());
//                    entity.setIco(item.getIco());
//                    bookmarkEntityDao.insert(entity);
//                    CustomToast.toast("添加成功");
//                } catch (Exception e) {
//                    CustomToast.toast("添加失败");
//                }
//                bottomSheetDialog.dismiss();
//            }
//        });
    }

    @OnClick(R.id.delete)
    public void delete() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_all_history, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity())
                .setView(view).create();
        mDialog.setCancelable(true);
        view.findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    historyDao.deleteAll();
                    CustomToast.toast("清除成功");
                    historyBeanList.clear();
                    historyAdapter.chageView(historyBeanList);
                    delete.setEnabled(false);
                    if (historyBeanList.size() > 0) {
                        no.setVisibility(View.INVISIBLE);
                        expandedMenu.setVisibility(View.VISIBLE);
                    } else {
                        no.setVisibility(View.VISIBLE);
                        expandedMenu.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                mDialog.dismiss();
            }
        });
        view.findViewById(R.id.clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
