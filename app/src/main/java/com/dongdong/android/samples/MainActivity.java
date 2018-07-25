package com.dongdong.android.samples;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dongdong.android.download.R;
import com.dongdong.android.samples.downloadlist.DownloadListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvDownlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvDownlist = (TextView) findViewById(R.id.item_download_list);
        onListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void onListener() {
        mTvDownlist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_download_list:
                Intent tolist = new Intent(MainActivity.this, DownloadListActivity.class);
                startActivity(tolist);
                break;
            default:
                break;
        }

    }
}
