package com.dongdong.android.download.api;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.dongdong.android.download.callback.DownloadListener;
import com.dongdong.android.download.callback.DownloadManager;
import com.dongdong.android.download.callback.Successlistener;
import com.dongdong.android.download.entity.DownloadInfo;
import com.dongdong.android.download.exception.DownloadException;
import com.dongdong.android.download.status.DownloadStatus;
import com.dongdong.android.download.thread.DownloadHandler;
import com.dongdong.android.download.thread.DownloadRunnable;
import com.dongdong.android.download.thread.DownloadThreadPool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.dom.DOMLocator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongdong.android.download.entity.DownloadInfo.TOTAL_ERROR;

public class DownloadManagerImpl implements DownloadManager, Successlistener {

    public static String TAG = "DownloadManagerImpl";
    private Context mContext;
    private String mAbsolutePath;
    private HashMap<String, DownloadInfo> mDownTaskQueue;
    private HashMap<String, DownloadListener> mDownListenerQueue;
    private HashMap<String, DownloadRunnable> mDownRunableQueue;
    private DownloadHandler mMainHandler;
    private OkHttpClient okHttpClient;


    private DownloadManagerImpl() {
        if (mDownTaskQueue == null) {
            mDownTaskQueue = new HashMap<>();
        } else {
            mDownTaskQueue.clear();
        }

        if (mDownListenerQueue == null) {
            mDownListenerQueue = new HashMap<>();
        } else {
            mDownListenerQueue.clear();
        }

        if (mDownRunableQueue == null) {
            mDownRunableQueue = new HashMap<>();
        } else {
            mDownRunableQueue.clear();
        }

        mMainHandler = new DownloadHandler(mDownListenerQueue, this);

        okHttpClient = new OkHttpClient();
    }

    @Override
    public void OnSuccess(String key) {
        removeQueue(key);
    }


    private static class DownloadManagerHolder {
        private final static DownloadManagerImpl instance = new DownloadManagerImpl();
    }

    public static DownloadManagerImpl getInstance(Context context) {
        DownloadManagerImpl manager = DownloadManagerHolder.instance;
        manager.get(context);
        return manager;
    }


    private void get(Context context) {
        if (mContext == null && context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (mContext == null) {
            this.mContext = context.getApplicationContext();
            mAbsolutePath = mContext.getExternalFilesDir("FileDownload").getAbsolutePath();
        }
    }

    @Override
    public void download(DownloadInfo downloadInfo, DownloadListener listener) {
        if (downloadInfo == null || TextUtils.isEmpty(downloadInfo.getUrl())) {
            throw new DownloadException(DownloadException.EXCEPTION_URL_NULL, "The DownloadInfo Url can't be empty");
        }
        if (listener == null) {
            throw new DownloadException(DownloadException.EXCEPTION_LISTENER_NULL, "The Listener can't be " +
                    "empty");
        }
        mDownListenerQueue.put(downloadInfo.getUrl(), listener);
        if (downloadInfo.getLength() == TOTAL_ERROR || TextUtils.isEmpty(downloadInfo.getPath())) {
            onPrepare(downloadInfo);
        } else {
            doDownloading(downloadInfo);
        }
    }

    @Override
    public void pause(DownloadInfo downloadInfo) {
        if (downloadInfo == null || TextUtils.isEmpty(downloadInfo.getUrl())) {
            throw new DownloadException(DownloadException.EXCEPTION_URL_NULL, "The DownloadInfo Url can't be empty");
        }
        DownloadRunnable downloadRunnable = mDownRunableQueue.get(downloadInfo.getUrl());
        if (downloadRunnable != null) {
            downloadRunnable.onPause(downloadRunnable.getTaskInThread());
            mDownRunableQueue.remove(downloadInfo.getUrl());
        }

    }

    @Override
    public void resume(DownloadInfo downloadInfo) {
        if (downloadInfo == null || TextUtils.isEmpty(downloadInfo.getUrl())) {
            throw new DownloadException(DownloadException.EXCEPTION_URL_NULL, "The DownloadInfo Url can't be empty");
        }
        doDownloading(downloadInfo);
    }

    @Override
    public void remove(DownloadInfo downloadInfo, DownloadListener downloadListener) {
        if (downloadInfo == null || TextUtils.isEmpty(downloadInfo.getUrl())) {
            throw new DownloadException(DownloadException.EXCEPTION_URL_NULL, "The DownloadInfo Url can't be empty");
        }
        if (downloadInfo == null || TextUtils.isEmpty(downloadInfo.getPath())) {
            throw new DownloadException(DownloadException.EXCEPTION_URL_NULL, "The DownloadInfo Path can't be empty");
        }
        DownloadRunnable downloadRunnable = mDownRunableQueue.get(downloadInfo.getUrl());
        if (downloadRunnable != null) {
            downloadRunnable.onRemove();
        }
        if (delFile(downloadInfo.getPath())) {
            removeQueue(downloadInfo.getUrl());
            downloadListener.onRemoved(downloadInfo.getUrl());
        }
    }

    @Override
    public void onDestroy() {

    }

    private void removeQueue(String key) {
        if (mDownTaskQueue != null) {
            mDownTaskQueue.remove(key);
        }

        if (mDownListenerQueue != null) {
            mDownListenerQueue.remove(key);
        }

        if (mDownRunableQueue != null) {
            mDownRunableQueue.remove(key);
        }
    }

    private void doDownloading(DownloadInfo downloadInfo) {
        mDownTaskQueue.put(downloadInfo.getUrl(), downloadInfo);
        if (downloadInfo.getExistSize() == downloadInfo.getLength()) {
            Message msg = mMainHandler.obtainMessage();
            msg.what = DownloadStatus.STATUS_SUCCESS;
            msg.obj = downloadInfo;
            mMainHandler.sendMessage(msg);
            return;
        }
        if (DownloadThreadPool.getInstance().isIdle()) {
            DownloadRunnable downrunnable = new DownloadRunnable(downloadInfo, mMainHandler, okHttpClient);
            DownloadThreadPool.getInstance().execute(downrunnable);
            mDownRunableQueue.put(downloadInfo.getUrl(), downrunnable);
        } else {

            DownloadException exception = new DownloadException(DownloadException
                    .EXCEPTION_DOWNLOAD_NUM_OUT, "The biggest download number no" +
                    " more than five");
            mDownListenerQueue.get(downloadInfo.getUrl()).onFailure(downloadInfo.getUrl(), exception);
        }

    }

    private void onPrepare(final DownloadInfo downloadInfo) {
        final String ketUrl = downloadInfo.getUrl();
        if (TextUtils.isEmpty(downloadInfo.getPath())) {
            String filePath = mAbsolutePath + "/" + downloadInfo.getUserName();
            downloadInfo.setPath(filePath);
            long filesize = getFileSize(filePath);
            if (filesize >= 0) {
                downloadInfo.setExistSize(filesize);
            } else {
                DownloadException exception = new DownloadException(DownloadException
                        .EXCEPTION_FILE_CREATE_ERROR, "The File creation failed");
                mDownListenerQueue.get(ketUrl).onFailure(ketUrl, exception);
            }

        }

        if (downloadInfo.getLength() == TOTAL_ERROR) {
            Request request = new Request.Builder()
                    .get()
                    .url(ketUrl)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    DownloadException exception = new DownloadException(DownloadException
                            .EXCEPTION_FILE_SIZE_ERROR, e.getMessage());
                    downloadInfo.setException(exception);
                    Message msg = mMainHandler.obtainMessage();
                    msg.what = DownloadStatus.STATUS_FAIL;
                    msg.obj = downloadInfo;
                    mMainHandler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        //判断请求是否取消
                        if (call.isCanceled()) {
                            DownloadException exception = new DownloadException(DownloadException
                                    .EXCEPTION_REQUEST_ERROR, "Network request failed");
                            downloadInfo.setException(exception);
                            Message msg = mMainHandler.obtainMessage();
                            msg.what = DownloadStatus.STATUS_FAIL;
                            msg.obj = downloadInfo;
                            mMainHandler.sendMessage(msg);
                            return;
                        }

                        if (response != null && response.isSuccessful()) {
                            long contentLength = response.body().contentLength();
                            contentLength = contentLength == 0 ? TOTAL_ERROR : contentLength;
                            if (contentLength != TOTAL_ERROR) {
                                downloadInfo.setLength(contentLength);
                                doDownloading(downloadInfo);
                            }
                        } else {
                            DownloadException exception = new DownloadException(DownloadException
                                    .EXCEPTION_REQUEST_ERROR, "Network request failed");
                            downloadInfo.setException(exception);
                            Message msg = mMainHandler.obtainMessage();
                            msg.what = DownloadStatus.STATUS_FAIL;
                            msg.obj = downloadInfo;
                            mMainHandler.sendMessage(msg);
                            return;
                        }


                    } catch (Exception e) {//发生异常，失败回调
                        DownloadException exception = new DownloadException(DownloadException
                                .EXCEPTION_REQUEST_ERROR, e.getMessage());
                        downloadInfo.setException(exception);
                        Message msg = mMainHandler.obtainMessage();
                        msg.what = DownloadStatus.STATUS_FAIL;
                        msg.obj = downloadInfo;
                        mMainHandler.sendMessage(msg);
                    } finally {//记得关闭操作
                        response.close();
                    }


                }
            });

        } else {
            doDownloading(downloadInfo);
        }


    }

    private long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            try {
                file.createNewFile();
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

    }

    private boolean delFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
        }

        return false;
    }


}
