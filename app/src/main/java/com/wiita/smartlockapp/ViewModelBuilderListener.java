package com.wiita.smartlockapp;

import java.util.ArrayList;

/**
 * Created by Wiita on 7/19/2017.
 */

public interface ViewModelBuilderListener {
    void onNewListOfModels(final ArrayList<HistoryListEventViewModel> newModels);
}
