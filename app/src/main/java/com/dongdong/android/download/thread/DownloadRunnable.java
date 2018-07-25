package com.dongdong.android.download.thread;

import android.os.Message;

import com.dongdong.android.download.entity.DownloadInfo;
import com.dongdong.android.download.exception.DownloadException;
import com.dongdong.android.download.status.DownloadStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadRunnable implements Runnable {

    DownloadHandler mainHandler;
    DownloadInfo info;
    String taskName;
    Thread taskInThread;
    OkHttpClient client;
    boolean ispause = false;
    boolean isremove = false;
    File file;
    long existsize;
    long lenght;
    InputStream is = null;
    RandomAccessFile savedFile = null;

    public DownloadRunnable(DownloadInfo info, DownloadHandler handler, OkHttpClient okHttpClient) {
        this.mainHandler = handler;
        this.info = info;
        this.taskName = info.getUrl();
        this.client = okHttpClient;
    }

    @Override
    public void run() {
        this.setTaskInThread(Thread.currentThread());
        file = new File(info.getPath());
        existsize = info.getExistSize();
        lenght = info.getLength();
        Request request = new Request.Builder()
                //断点续传要用到的，指示下载的区间
                .addHeader("RANGE", "bytes=" + existsize + "-")
                .url(info.getUrl())
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                //跳过已经下载的字节
                savedFile.seek(existsize);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isremove) {
                        return;
                    }
                    if (ispause) {
                        info.setExistSize(existsize + total);
                        Message msg = mainHandler.obtainMessage();
                        msg.what = DownloadStatus.STATUS_PAUSE;
                        msg.obj = info;
                        mainHandler.sendMessage(msg);
                        return;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        //计算已经下载的百分比
                        int progress = (int) ((total + existsize) * 100 / lenght);
                        //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                        //可以调用publishProgress()方法完成。
                        Message msg = mainHandler.obtainMessage();
                        if ((total + existsize) == lenght) {
                            info.setExistSize(existsize + total);
                            info.setProgress(100);
                            msg.what = DownloadStatus.STATUS_SUCCESS;
                        } else {
                            info.setProgress(progress);
                            msg.what = DownloadStatus.STATUS_RUNNING;
                        }
                        msg.obj = info;
                        mainHandler.sendMessage(msg);
                    }

                }
                response.body().close();
            }

        } catch (IOException e) {
            DownloadException exception = new DownloadException(DownloadException
                    .EXCEPTION_FILE_PARSING_ERROR, e.getMessage());
            info.setException(exception);
            Message msg = mainHandler.obtainMessage();
            msg.what = DownloadStatus.STATUS_FAIL;
            msg.obj = info;
            mainHandler.sendMessage(msg);
        } catch (Exception e) {
            DownloadException exception = new DownloadException(DownloadException
                    .EXCEPTION_FILE_PARSING_ERROR, e.getMessage());
            info.setException(exception);
            Message msg = mainHandler.obtainMessage();
            msg.what = DownloadStatus.STATUS_FAIL;
            msg.obj = info;
            mainHandler.sendMessage(msg);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        return;
    }


    public void onPause(Thread thread) {
        ispause = true;
    }

    public void onRemove() {
        isremove = true;
    }

    public Thread getTaskInThread() {
        return taskInThread;
    }

    public void setTaskInThread(Thread taskInThread) {
        this.taskInThread = taskInThread;
    }


}
