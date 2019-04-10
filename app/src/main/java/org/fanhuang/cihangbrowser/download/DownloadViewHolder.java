package org.fanhuang.cihangbrowser.download;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;

/**
 * Created by Lan on 2017-3-27.
 */

public abstract class DownloadViewHolder extends RecyclerView.ViewHolder {
    protected DownloadInfo downloadInfo;

    public DownloadViewHolder(View view, DownloadInfo downloadInfo) {
        super(view);
        this.downloadInfo = downloadInfo;
        x.view().inject(this, view);
    }

    public DownloadViewHolder(View view) {
        super(view);
        x.view().inject(this, view);
    }

    public final DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void update(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public abstract void onWaiting();

    public abstract void onStarted();

    public abstract void onLoading(long total, long current);

    public abstract void onSuccess(File result);

    public abstract void onError(Throwable ex, boolean isOnCallback);

    public abstract void onCancelled(Callback.CancelledException cex);
}
