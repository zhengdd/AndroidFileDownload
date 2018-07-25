package com.dongdong.android.samples.downloadlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.dongdong.android.download.R;
import com.dongdong.android.download.api.DownloadManagerImpl;
import com.dongdong.android.download.callback.DownloadListener;
import com.dongdong.android.download.entity.DownloadInfo;
import com.dongdong.android.download.exception.DownloadException;
import com.dongdong.android.download.status.DownloadStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author dongdongzheng
 */
public class DownloadListActivity extends AppCompatActivity implements View.OnClickListener {

    Button mBtnAddItem;


    RecyclerView mRecycleList;


    private List<DownloadInfo> list;
    private DownloadAdapter adapter;
    private HashMap<String, Integer> positionMap;
    String[] strpath = {"http://imtt.dd.qq.com/16891/2D54AB7D3909BFCE37550C055212E256.apk",
            "http://imtt.dd.qq.com/16891/5C64C9AEF9046D107B47887E7D32260B.apk",
            "http://imtt.dd.qq.com/16891/AA02DF7EB37E9188CCBFAB0DC9E35B71.apk",
            "http://imtt.dd.qq.com/16891/0B42E2147FF8492CCD766D064E1F4076.apk"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_list_activity);
        initView();
        initData();
        initListener();
    }

    public void initView() {
        mBtnAddItem = (Button) findViewById(R.id.down_load_list_btn_add_item);
        mRecycleList = (RecyclerView) findViewById(R.id.down_load_list_recycle_list);

        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        if (positionMap == null) {
            positionMap = new HashMap<>();
        } else {
            positionMap.clear();
        }

        adapter = new DownloadAdapter(this.getBaseContext(), list, adapterlistener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getBaseContext());
        mRecycleList.setLayoutManager(linearLayoutManager);
        mRecycleList.setAdapter(adapter);

    }

    public void initData() {

    }

    public void initListener() {
        mBtnAddItem.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_load_list_btn_add_item:
                for (int i = 0; i < strpath.length; i++) {
                    DownloadInfo info = new DownloadInfo();
                    info.setProgress(0);
                    info.setStatus(DownloadStatus.STATUS_INIT);
                    info.setUrl(strpath[i]);
                    list.add(info);
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

    View.OnClickListener adapterlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            positionMap.put(list.get(position).getUrl(), position);
            switch (list.get(position).getStatus()) {
                case DownloadStatus.STATUS_INIT:
                    DownloadManagerImpl.getInstance(getApplicationContext()).download(list.get(position),
                            downloadListener);
                    break;
                case DownloadStatus.STATUS_RUNNING:
                    DownloadManagerImpl.getInstance(getApplicationContext()).pause(list.get(position));
                    break;
                case DownloadStatus.STATUS_PAUSE:
                    DownloadManagerImpl.getInstance(getApplicationContext()).resume(list.get(position));
                    break;
                case DownloadStatus.STATUS_SUCCESS:
                    DownloadManagerImpl.getInstance(getApplicationContext()).remove(list.get
                            (position), downloadListener);
                    break;
                default:
                    break;
            }
        }
    };

    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onStart(String url) {

        }

        @Override
        public void onSuccess(String url, String path) {
            int position = positionMap.get(url);
            list.get(position).setPath(path);
            list.get(position).setStatus(DownloadStatus.STATUS_SUCCESS);
            list.get(position).setProgress(100);
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onFailure(String url, DownloadException e) {
            int position = positionMap.get(url);
            list.get(position).setStatus(DownloadStatus.STATUS_FAIL);
            list.get(position).setException(e);
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onPaused(DownloadInfo downloadInfo) {
            int position = positionMap.get(downloadInfo.getUrl());
            list.get(position).setStatus(DownloadStatus.STATUS_PAUSE);
            list.get(position).setLength(downloadInfo.getLength());
            list.get(position).setUserName(downloadInfo.getUserName());
            list.get(position).setExistSize(downloadInfo.getExistSize());
            list.get(position).setPath(downloadInfo.getPath());
            list.get(position).setProgress(downloadInfo.getProgress());
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onRemoved(String url) {
            int position = positionMap.get(url);
            list.get(position).setStatus(DownloadStatus.STATUS_REMOVE);
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onDownloading(String url, int progress, long size) {
            int position = positionMap.get(url);
            list.get(position).setStatus(DownloadStatus.STATUS_RUNNING);
            list.get(position).setProgress(progress);
            adapter.notifyItemChanged(position);
        }
    };
}