package com.dongdong.android.download.callback;

import com.dongdong.android.download.entity.DownloadInfo;
import com.dongdong.android.download.exception.DownloadException;

public interface DownloadListener {

    void onStart(String url);

    void onSuccess(String url, String path);

    void onFailure(String url, DownloadException e);

    void onPaused(DownloadInfo downloadInfo);

    void onRemoved(String url);

    void onDownloading(String url, int progress, long size);

}
