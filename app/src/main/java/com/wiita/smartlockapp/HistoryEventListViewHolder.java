package com.wiita.smartlockapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Wiita on 7/16/2017.
 */

public class HistoryEventListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView header;
    private TextView subheader;
    private TextView date;
    private ImageView image;
    private Context context;
    private ProgressBar progressBar;
    private HistoryListEventViewModel model;

    public HistoryEventListViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        header = (TextView)itemView.findViewById(R.id.summary_list_viewholder_header);
        subheader = (TextView)itemView.findViewById(R.id.summary_list_viewholder_subheader);
        date = (TextView)itemView.findViewById(R.id.summary_list_viewholder_date);
        image = (ImageView)itemView.findViewById(R.id.summary_list_viewholder_image);
        progressBar = (ProgressBar)itemView.findViewById(R.id.summary_list_viewholder_progressbar);
    }

    public void bind(HistoryListEventViewModel model){
        this.model = model;
        header.setText(model.header);
        subheader.setText(model.subHeader);
        date.setText(model.date);
        Glide.with(context).asGif().load(model.url).apply(RequestOptions.circleCropTransform()).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(image);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new HistoryEventClickEvent(model));
    }
}
