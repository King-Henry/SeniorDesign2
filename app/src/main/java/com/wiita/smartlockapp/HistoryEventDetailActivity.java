package com.wiita.smartlockapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class HistoryEventDetailActivity extends AppCompatActivity implements RequestListener{

    private ImageView imageView;
    private TextView header;
    private TextView subheader;
    private TextView date;
    private TextView time;
    private Bundle bundle;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_event_detail);
        imageView = (ImageView)findViewById(R.id.history_event_detail_imageview);
        header = (TextView)findViewById(R.id.history_event_detail_header);
        subheader = (TextView)findViewById(R.id.history_event_detail_subheader);
        date = (TextView)findViewById(R.id.history_event_detail_date);
        time = (TextView)findViewById(R.id.history_event_detail_time);
        progressBar = (ProgressBar)findViewById(R.id.history_event_detail_progressbar);
        Intent intent = getIntent();
        if(intent != null){
            bundle = intent.getExtras();
        }
        if(bundle != null){
            Glide.with(this).load(getIntent().getExtras().getString(MainActivity.HISTORY_EVENT_DETAIL_IMAGE_URL)).listener(this).into(imageView);
            header.setText(bundle.getString(MainActivity.HISTORY_EVENT_DETAIL_HEADER));
            subheader.setText(bundle.getString(MainActivity.HISTORY_EVENT_DETAIL_SUBHEADER));
            date.setText(bundle.getString(MainActivity.HISTORY_EVENT_DETAIL_DATE));
            time.setText(bundle.getString(MainActivity.HISTORY_EVENT_DETAIL_TIME));
        }

    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
        progressBar.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
        progressBar.setVisibility(View.GONE);
        return false;
    }
}
