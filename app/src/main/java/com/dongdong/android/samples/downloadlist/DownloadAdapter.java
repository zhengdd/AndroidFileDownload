package com.dongdong.android.samples.downloadlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dongdong.android.download.R;
import com.dongdong.android.download.entity.DownloadInfo;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private List<DownloadInfo> list;
    private Context mContext;
    private View.OnClickListener listener;

    public DownloadAdapter(Context context, List<DownloadInfo> list, View.OnClickListener listener) {
        this.mContext = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.download_list_item_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        if (listener != null) {
            holder.mBtnStatus.setOnClickListener(listener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DownloadInfo info = list.get(position);
        holder.mTvName.setText(info.getUserName());
        holder.mBtnStatus.setTag(position);
        holder.mTvPosition.setText(info.getProgress() + "");
        switch (info.getStatus()) {
            case 0:
            case 1:
                holder.mBtnStatus.setText("开始");
                holder.mProgress.setProgress(info.getProgress());
                break;
            case 2:
                holder.mBtnStatus.setText("暂停");
                holder.mProgress.setProgress(info.getProgress());
                break;
            case 3:

                holder.mTvPosition.setText("任务已清除");
                break;
            case 4:
                holder.mTvPosition.setText("任务失败");
                break;
            case 5:
                holder.mBtnStatus.setText("完成");
                holder.mProgress.setProgress(info.getProgress());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            DownloadInfo info = list.get(position);
            holder.mTvName.setText(info.getUserName());
            holder.mTvPosition.setText(info.getProgress() + "");
            holder.mBtnStatus.setTag(position);
            switch (info.getStatus()) {
                case 0:
                case 1:
                    holder.mBtnStatus.setText("开始");
                    holder.mProgress.setProgress(info.getProgress());
                    break;
                case 2:
                    holder.mBtnStatus.setText("暂停");
                    holder.mProgress.setProgress(info.getProgress());
                    break;
                case 3:

                    holder.mTvPosition.setText("任务已清除");
                    break;
                case 4:
                    holder.mTvPosition.setText("任务失败");
                    break;
                case 5:
                    holder.mBtnStatus.setText("完成");
                    holder.mProgress.setProgress(info.getProgress());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private Button mBtnStatus;
        private ProgressBar mProgress;
        private TextView mTvPosition;

        public ViewHolder(View view) {
            super(view);
            mTvName = (TextView) view.findViewById(R.id.down_load_item_tv_name);
            mBtnStatus = (Button) view.findViewById(R.id.down_load_item_tv_start);
            mProgress = (ProgressBar) view.findViewById(R.id.down_load_item_progress_bar);
            mTvPosition = (TextView) view.findViewById(R.id.down_load_item_tv_position);
        }

    }
}
