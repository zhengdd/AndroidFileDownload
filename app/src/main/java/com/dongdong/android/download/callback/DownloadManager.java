package com.dongdong.android.download.callback;

import com.dongdong.android.download.entity.DownloadInfo;

public interface DownloadManager {

    void download(DownloadInfo downloadInfo, DownloadListener listener);

    void pause(DownloadInfo downloadInfo);

    void resume(DownloadInfo downloadInfo);

    void remove(DownloadInfo downloadInfo, DownloadListener listen);

    void onDestroy();

}
