package com.wiita.smartlockapp;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;

/**
 * Created by Wiita on 7/17/2017.
 */

public class SummaryListViewModelBuilder {

    private HandlerThread handlerThread;
    private Handler handler;
    private ArrayList<SummaryListEventViewModel> viewModels = new ArrayList<>();

    public SummaryListViewModelBuilder(){

        handlerThread = new HandlerThread("SUMMARY_LIST_HANDLER");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        buildModels();
    }

    private void buildModels(){

    }

    
}
