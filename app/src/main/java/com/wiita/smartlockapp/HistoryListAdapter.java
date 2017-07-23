package com.wiita.smartlockapp;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Wiita on 7/18/2017.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryEventListViewHolder> implements ViewModelBuilderListener{

    private HistoryListViewModelBuilder modelBuilder;
    private ArrayList<HistoryListEventViewModel> models = new ArrayList<>();
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private HistoryListAdapterListener listAdapterListener;

    public HistoryListAdapter(HistoryListAdapterListener listener) {
        modelBuilder = new HistoryListViewModelBuilder(this);
        listAdapterListener = listener;
    }

    @Override
    public HistoryEventListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("HistoryListAdapter", "onCreateViewHolder");
        View viewHolderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_viewholder_layout,parent,false);
        return new HistoryEventListViewHolder(viewHolderView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(HistoryEventListViewHolder holder, int position) {
        Log.d("HistoryListAdapter", "onBindViewHolder");
        holder.bind(models.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d("models size", "" + models.size());
        return models.size();
    }

    public void buildModels(ArrayList<HistoryEvent> events){
        modelBuilder.buildModels(events);
    }

    @Override
    public void onNewListOfModels(final ArrayList<HistoryListEventViewModel> newModels){
        Log.d("HistoryListAdapter", "onNewListOfModels");
        Log.d("HistoryListAdapter: ", "" + newModels.size());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                models.clear();
                models.addAll(newModels);
                listAdapterListener.updateView();
                notifyDataSetChanged();
                Log.d("HistoryListAdapter", "new models added");
            }
        });
    }
}
