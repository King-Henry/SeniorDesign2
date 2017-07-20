package com.wiita.smartlockapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Wiita on 7/16/2017.
 */

public class SummaryListViewHolder extends RecyclerView.ViewHolder {

    private TextView header;
    private TextView subheader;
    private TextView date;
    private ImageView image;
    private Context context;

    public SummaryListViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        header = (TextView)itemView.findViewById(R.id.summary_list_viewholder_header);
        subheader = (TextView)itemView.findViewById(R.id.summary_list_viewholder_subheader);
        date = (TextView)itemView.findViewById(R.id.summary_list_viewholder_date);
        image = (ImageView)itemView.findViewById(R.id.summary_list_viewholder_image);
    }

    public void bind(SummaryListEventViewModel model){
        header.setText(model.header);
        subheader.setText(model.subHeader);
        date.setText(model.date);
        Glide.with(context).load(model.url).into(image);
    }
}
