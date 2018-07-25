package com.dongdong.android.download.exception;

public class DownloadException extends RuntimeException {

    public static final int EXCEPTION_URL_NULL = 0;
    public static final int EXCEPTION_LISTENER_NULL = 1;
    public static final int EXCEPTION_PATH_NULL = 2;
    public static final int EXCEPTION_FILE_SIZE_ERROR = 3;
    public static final int EXCEPTION_FILE_PARSING_ERROR = 4;
    public static final int EXCEPTION_FILE_CREATE_ERROR = 5;
    public static final int EXCEPTION_REQUEST_ERROR = 6;
    public static final int EXCEPTION_DOWNLOAD_NUM_OUT = 7;
    public static final int EXCEPTION_DOWNLOAD_ERROR = 8;


    public DownloadException(int code) {
        super();
        this.code = code;
    }


    public DownloadException(int code, String message) {
        super(message);
        this.code = code;
    }

    public DownloadException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


    public DownloadException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
