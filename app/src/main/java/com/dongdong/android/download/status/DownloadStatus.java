package com.dongdong.android.download.status;

public class DownloadStatus {

    /**
     * 初始状态
     */
    public static final int STATUS_INIT = 0;

    /**
     * 暂停等待状态
     */
    public static final int STATUS_PAUSE = 1;

    /**
     * 下载进行中
     */
    public static final int STATUS_RUNNING = 2;

    /**
     * 被清除状态
     */
    public static final int STATUS_REMOVE = 3;

    /**
     * 下载失败
     */
    public static final int STATUS_FAIL = 4;

    /**
     * 下载成功
     */
    public static final int STATUS_SUCCESS = 5;

}
