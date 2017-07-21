package com.wiita.smartlockapp;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Wiita on 7/17/2017.
 */

public class HistoryListViewModelBuilder {

    private HandlerThread handlerThread;
    private Handler handler;
    private ViewModelBuilderListener listener;

    public HistoryListViewModelBuilder(ViewModelBuilderListener listener){
        handlerThread = new HandlerThread("SUMMARY_LIST_HANDLER");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        this.listener = listener;
    }

    public void buildModels(final ArrayList<HistoryEvent> events){
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<HistoryListEventViewModel> viewModels = new ArrayList<HistoryListEventViewModel>();
                for(HistoryEvent event: events){
                    HistoryListEventViewModel model = new HistoryListEventViewModel();
                    model.date = event.date;
                    model.header = event.header;
                    model.time = event.time;
                    model.url = event.url;
                    model.subHeader = event.subheader;
                    viewModels.add(model);
                    Log.d("SummaryListViewModelBui", "model built");
                }
                listener.onNewListOfModels(viewModels);
                Log.d("SummaryListViewModelBui", "notifying listener of new models built");
            }
        });
    }

    
}
