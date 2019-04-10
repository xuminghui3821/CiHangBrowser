package org.fanhuang.cihangbrowser.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.CompoundButton;
import com.squareup.picasso.Picasso;

import org.fanhuang.cihangbrowser.R;
import org.fanhuang.cihangbrowser.activity.NewDownloadActiviy;
import org.fanhuang.cihangbrowser.app.MyAppAction;
import org.fanhuang.cihangbrowser.database.SharedPreferencesUtils;
import org.fanhuang.cihangbrowser.download.DownloadInfo;
import org.fanhuang.cihangbrowser.download.DownloadManager;
import org.fanhuang.cihangbrowser.download.DownloadState;
import org.fanhuang.cihangbrowser.download.DownloadViewHolder;
import org.fanhuang.cihangbrowser.interfaces.NetWorkCallBack;
import org.fanhuang.cihangbrowser.java_bean.DownloadHot;
import org.fanhuang.cihangbrowser.network.AppInfo;
import org.fanhuang.cihangbrowser.network.Commdbase;
import org.fanhuang.cihangbrowser.network.Config;
import org.fanhuang.cihangbrowser.utils.CustomToast;
import org.fanhuang.cihangbrowser.utils.SystemUtils;
import org.fanhuang.cihangbrowser.utils.Utlis;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jameson.io.library.util.LogUtils;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by xiaohuihui on 2018/12/6.
 */

public class DownloadFragment extends SupportFragment implements NetWorkCallBack {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.download)
    RecyclerView download;
    @BindView(R.id.l2)
    LinearLayout l2;
    @BindView(R.id.appIcon)
    ImageView appIcon;
    @BindView(R.id.fileName)
    TextView fileName;
    @BindView(R.id.download_pb)
    ProgressBar downloadPb;
    @BindView(R.id.fileSize)
    TextView fileSize;
    @BindView(R.id.speed)
    TextView speed;
    @BindView(R.id.action)
    TextView action;
    @BindView(R.id.downloaded)
    RecyclerView downloaded;
    @BindView(R.id.l1)
    LinearLayout l1;
    @BindView(R.id.cher)
    TextView cher;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DownloadManager downloadManager;
    private OnFragmentInteractionListener mListener;
    private DownloadListAdapter downloadListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Timer timer;
    private List<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
    private List<DownloadInfo> downloadedInfoList = new ArrayList<DownloadInfo>();
    private DonloadedAdapter mAdapter;
    private List<DownloadHot> downloadHots = new ArrayList<>();
    //app信息对象的列表
    private List<AppInfo> appList = new ArrayList<>();
    private String fileSavePath;//已下载中,点击的文件路径
    private String fileType;//点击的文件类型,apk,txt,mp3
    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".JPEG", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public DownloadFragment() {

    }

    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static DownloadFragment newInstance(String param1, String param2) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
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
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        ButterKnife.bind(this, view);
        init();
        initData();
        return view;
    }


    private void init() {
        downloadManager = DownloadManager.getInstance();
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        download.setItemAnimator(new DefaultItemAnimator());
        download.setLayoutManager(linearLayoutManager);
        download.setHasFixedSize(true);
        // download.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        manager.setAutoMeasureEnabled(true);
        downloaded.setLayoutManager(manager);
        downloaded.setHasFixedSize(true);
        //   downloaded.setNestedScrollingEnabled(false);
        downloaded.setItemAnimator(new DefaultItemAnimator());

    }

    private void initData() {
        downloadInfoList.clear();
        downloadedInfoList.clear();
        for (int i = 0; i < downloadManager.getDownloadInfoList().size(); i++) {
            DownloadInfo down = downloadManager.getDownloadInfo(i);
            if (downloadManager.getDownloadInfo(i).getState() != DownloadState.FINISHED) {
                downloadInfoList.add(down);
            } else {
                downloadedInfoList.add(down);
            }
        }
        if (downloadListAdapter == null) {
            downloadListAdapter = new DownloadListAdapter();
            download.setAdapter(downloadListAdapter);
        } else {
            downloadListAdapter.notifyDataSetChanged();
        }
        if (mAdapter == null) {
            mAdapter = new DonloadedAdapter();
            downloaded.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        if (downloadInfoList.size() > 0) {
            download.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);
        } else {
            download.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        }
        if (downloadedInfoList.size() > 0) {
            cher.setVisibility(View.VISIBLE);
        } else {
            cher.setVisibility(View.INVISIBLE);
        }
    }

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
    public void onSuccess(int action, final String response) {
//        try {
//            if (action != 2) {
//                downloadHots = JSON.parseArray(response, DownloadHot.class);
//                final DownloadHot item = downloadHots.get(0);
//                fileName.setText(item.getName());
//                speed.setText(item.getSize());
//                fileSize.setText(item.getClassify());
//                Picasso.with(getContext()).load(item.getLogo()).into(appIcon);
//                this.action.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String a = (String) SharedPreferencesUtils.get(getContext(), "download", Config.FILE_PATH);
//                        String label = item.getName() + ".apk";
//                        try {
//                            DownloadManager.getInstance().startDownload(item.getUrl(), label, a + label, true, false, null);
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                            CustomToast.toast("添加下载失败");
//                        }
//                        getContext().startActivity(new Intent(getContext(), NewDownloadActiviy.class));
//                    }
//                });
//            }
//        } catch (Exception e) {
//
//        }

    }

    @Override
    public void onError(int action, String error) {

    }

    @OnClick(R.id.cher)
    public void cher(View view) {
        try {
            for (int i = 0; i < downloadedInfoList.size(); i++) {
                downloadManager.removeDownload(downloadedInfoList.get(i));
                CustomToast.toast("清除成功");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        initData();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class DownloadListAdapter extends RecyclerView.Adapter<DownloadItemViewHolder> {

        private Context mContext;
        private final LayoutInflater mInflater;

        private DownloadListAdapter() {
            mContext = getContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public DownloadItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_download_new, parent, false);
            return new DownloadItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DownloadItemViewHolder holder, int position) {
            final DownloadInfo downloadInfo = downloadInfoList.get(position);
            //DownloadInfo downloadInfo = downloadManager.getDownloadInfo(position);
            holder.update(downloadInfo);
            if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
                try {
                    downloadManager.startDownload(
                            downloadInfo.getUrl(),
                            downloadInfo.getLabel(),
                            downloadInfo.getFileSavePath(),
                            downloadInfo.isAutoResume(),
                            downloadInfo.isAutoRename(),
                            holder);
                } catch (DbException ex) {
                    ex.printStackTrace();
                    Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                }
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    final View views = LayoutInflater.from(getContext()).inflate(R.layout.view_chearn, null);
                    bottomSheetDialog.contentView(views);
                    bottomSheetDialog.inDuration(200);
                    bottomSheetDialog.outDuration(200);
                    bottomSheetDialog.cancelable(true);
                    bottomSheetDialog.show();
                    TextView button = (TextView) views.findViewById(R.id.cancel);
                    TextView button1 = (TextView) views.findViewById(R.id.query);
                    TextView textView = (TextView) views.findViewById(R.id.title);
                    textView.setText("是否终止下载，并删除任务？");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                downloadManager.removeDownload(downloadInfo);
                                CustomToast.toast("清除成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                                CustomToast.toast("清除失败");
                            }
                            initData();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return downloadInfoList.size();
        }
    }

    public class DownloadItemViewHolder extends DownloadViewHolder {
        @ViewInject(R.id.appIcon)
        ImageView appIcon;
        @ViewInject(R.id.fileName)
        TextView fileName;
        @ViewInject(R.id.download_pb)
        ProgressBar downloadPb;
        @ViewInject(R.id.fileSize)
        TextView fileSize;
        @ViewInject(R.id.speed)
        TextView speed;
        @ViewInject(R.id.action)
        TextView action;
        long oldCurrent = 0;

        public DownloadItemViewHolder(View view) {
            super(view);
            // refresh();
        }

        @Event(R.id.action)
        private void toggleEvent(View view) {
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.stopDownload(downloadInfo);
                    action.setText(x.app().getString(R.string.start));
                    break;
                case ERROR:
                case STOPPED:
                    try {
                        downloadManager.startDownload(
                                downloadInfo.getUrl(),
                                downloadInfo.getLabel(),
                                downloadInfo.getFileSavePath(),
                                downloadInfo.isAutoResume(),
                                downloadInfo.isAutoRename(),
                                this);
                    } catch (DbException ex) {
                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                    }
                    action.setText(x.app().getString(R.string.stop));
                    break;
                case FINISHED:
//                    try {
//                        downloadManager.startDownload(
//                                downloadInfo.getUrl(),
//                                downloadInfo.getLabel(),
//                                downloadInfo.getFileSavePath(),
//                                downloadInfo.isAutoResume(),
//                                downloadInfo.isAutoRename(),
//                                this);
//                    } catch (DbException ex) {
//                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
//                    }
                    break;
                default:
                    break;
            }
        }


        @Override
        public void update(DownloadInfo downloadInfo) {
            super.update(downloadInfo);
            refresh();
        }

        @Override
        public void onWaiting() {
            refresh();
        }

        @Override
        public void onStarted() {
            refresh();
        }

        @Override
        public void onLoading(long total, long current) {//下载相应
            refresh();
        }

        @Override
        public void onSuccess(File result) {//下载成功事件
            refresh();
            initData();
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            refresh();
        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            refresh();
        }

        public void refresh() {
            fileName.setText(downloadInfo.getLabel());
            downloadPb.setProgress(downloadInfo.getProgress());
            DownloadState state = downloadInfo.getState();
            long current;
            final double Progress = downloadInfo.getProgress();
            if (downloadInfo.getProgress() == 0) {
                current = 0;
            } else {
                current = (long) (downloadInfo.getFileLength() * Progress / 100);
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    oldCurrent = (long) (downloadInfo.getFileLength() * Progress / 100);
                }
            }, 1000);
            String fileLength = Utlis.convertFileSize(current) + "/" + Utlis.convertFileSize(downloadInfo.getFileLength());
            fileSize.setText(fileLength);
            speed.setText(String.valueOf(Utlis.convertFileSize(current - oldCurrent) + "/S"));
            speed.setVisibility(View.VISIBLE);
            switch (state) {
                case WAITING:
                case STARTED:
                    action.setText(x.app().getString(R.string.stop));
                    break;
                case ERROR:
                case STOPPED:
                    action.setText(x.app().getString(R.string.start));
                    speed.setVisibility(View.INVISIBLE);
                    break;
                case FINISHED://下载完成
                    //  stopBtn.setVisibility(View.INVISIBLE);
                    //  stopBtn.setVisibility(View.INVISIBLE);
                    timer.cancel();
                    //   downloadListAdapter.notifyDataSetChanged();
                    break;
                default:
                    action.setText(x.app().getString(R.string.start));
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    class DonloadedAdapter extends RecyclerView.Adapter<DonloadedAdapter.MyViewHoder> {

        private Context mContext;
        private final LayoutInflater mInflater;

        private DonloadedAdapter() {
            mContext = getContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_downloaded, parent, false);
            return new MyViewHoder(view);
        }

        public Drawable getApkIcon(String apkPath) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                try {
                    return appInfo.loadIcon(pm);
                } catch (OutOfMemoryError e) {
                    Log.e("ApkIconLoader", e.toString());
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final MyViewHoder holder, int position) {
            final DownloadInfo downloadInfo = downloadedInfoList.get(position);
            holder.fileName.setText(downloadInfo.getLabel());
            String fileLength = Utlis.convertFileSize(downloadInfo.getFileLength());
            holder.fileSize.setText(fileLength);

            //通过软件的后缀名来设置图标
            String fName = downloadInfo.getFileSavePath();
            int dotIndex = fName.lastIndexOf(".");
            if (dotIndex > 0) {//根据后缀名设置软件图标
                String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
                if (fileType.equals(".apk")) {
                    Drawable icon = getApkIcon(downloadInfo.getFileSavePath());
                    if (icon != null)
                        holder.appIcon.setImageDrawable(icon);
                    else
                        holder.appIcon.setImageResource(R.mipmap.ico_apk);
                } else if (fileType.equals(".pdf")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_pdf);
                } else if (fileType.equals(".jpeg") || fileType.equals(".bmp") || fileType.equals(".jpg") || fileType.equals(".jpg") || fileType.equals(".png")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_pic);
                } else if (fileType.equals(".avi") || fileType.equals(".mp3") || fileType.equals(".mp4") || fileType.equals(".jpg")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_pic);
                } else if (fileType.equals(".txt")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_txt);
                } else if (fileType.equals(".ppt") || fileType.equals(".pps") || fileType.equals(".pptx")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_ppt);
                } else if (fileType.equals(".rar") || fileType.equals(".zip")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_yasuo);
                } else if (fileType.equals(".htm") || fileType.equals(".html")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_html);
                } else if (fileType.equals(".doc") || fileType.equals(".docx")) {
                    holder.appIcon.setImageResource(R.mipmap.ico_doc);
                } else {
                    holder.appIcon.setImageResource(R.mipmap.ico_file);
                }
            }

            if (downloadInfo.getCreateTime() != null) {
                holder.speed.setText(Utlis.getDataTimess(downloadInfo.getCreateTime()));
            }
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//兼容7.0以上的版本
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                    }
                    File file = new File("file://" + downloadInfo.getFileSavePath());
                    Uri uri = Uri.fromFile(new File(downloadInfo.getFileSavePath()));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setAction(Intent.ACTION_VIEW);//动作，查看
                    intent.setDataAndType(uri, getMIMEType(file));//设置类型
                    startActivity(intent);
                }

            });
        }


        @Override
        public int getItemCount() {
            return downloadedInfoList.size();
        }

        class MyViewHoder extends RecyclerView.ViewHolder {
            @BindView(R.id.appIcon)
            ImageView appIcon;
            @BindView(R.id.fileName)
            TextView fileName;
            @BindView(R.id.download_pb)
            ProgressBar downloadPb;
            @BindView(R.id.fileSize)
            TextView fileSize;
            @BindView(R.id.speed)
            TextView speed;
            @BindView(R.id.action)
            TextView action;

            public MyViewHoder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
