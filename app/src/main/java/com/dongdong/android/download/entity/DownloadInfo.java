package com.dongdong.android.download.entity;

import android.text.TextUtils;

import com.dongdong.android.download.exception.DownloadException;
import com.dongdong.android.download.status.DownloadStatus;

import java.io.Serializable;

public class DownloadInfo implements Serializable {

    public static final long TOTAL_ERROR = -1;

    String url;

    String userName;

    String path;

    long length;

    long existSize;

    int progress;

    int status;

    DownloadException exception;

    public DownloadInfo() {
        length = TOTAL_ERROR;
        existSize = 0L;
        progress = 0;
        status = DownloadStatus.STATUS_INIT;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            if (url.lastIndexOf("/") + 1 < url.length()) {
                userName = url.substring(url.lastIndexOf("/") + 1);
            }
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getExistSize() {
        return existSize;
    }

    public void setExistSize(long existSize) {
        this.existSize = existSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DownloadException getException() {
        return exception;
    }

    public void setException(DownloadException exception) {
        this.exception = exception;
    }
}
