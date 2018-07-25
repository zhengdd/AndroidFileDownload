package com.dongdong.android.download.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dongdong.android.download.callback.DownloadListener;
import com.dongdong.android.download.callback.Successlistener;
import com.dongdong.android.download.entity.DownloadInfo;
import com.dongdong.android.download.status.DownloadStatus;

import java.util.HashMap;

public class DownloadHandler extends Handler {

    private HashMap<String, DownloadListener> mDownListenerQueue;
    private Successlistener successlistener;

    public DownloadHandler(HashMap<String, DownloadListener> mDownListenerQueue, Successlistener
            successlistener) {
        super(Looper.getMainLooper());
        this.mDownListenerQueue = mDownListenerQueue;
        this.successlistener = successlistener;
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        DownloadInfo info = (DownloadInfo) msg.obj;
        String keyUrl = info.getUrl();
        DownloadListener listener = mDownListenerQueue.get(keyUrl);
        if (listener != null) {
            switch (msg.what) {
                case DownloadStatus.STATUS_INIT:
                    break;
                case DownloadStatus.STATUS_PAUSE:
                    listener.onPaused(info);
                    break;
                case DownloadStatus.STATUS_RUNNING:
                    listener.onDownloading(keyUrl, info.getProgress(), info.getExistSize());
                    break;
                case DownloadStatus.STATUS_REMOVE:
                    listener.onRemoved(keyUrl);
                    break;
                case DownloadStatus.STATUS_FAIL:
                    listener.onFailure(keyUrl, info.getException());
                    break;
                case DownloadStatus.STATUS_SUCCESS:
                    listener.onSuccess(keyUrl, info.getPath());
                    successlistener.OnSuccess(keyUrl);
                    break;
                default:
                    break;
            }
        }
    }
}
