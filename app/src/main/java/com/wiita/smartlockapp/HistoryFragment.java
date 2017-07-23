package com.wiita.smartlockapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import static android.view.View.GONE;

public class HistoryFragment extends Fragment implements EventsDatabaseHandler.EventsDatabaseListener, HistoryListAdapterListener{

    private OnHistoryFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private HistoryListAdapter adapter;
    private EventsDatabaseHandler eventsDatabaseHandler;
    private ProgressBar progressBar;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.history_list_fragment_recyclerview);
        progressBar = (ProgressBar)view.findViewById(R.id.history_list_progressbar);
        adapter = new HistoryListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        eventsDatabaseHandler = new EventsDatabaseHandler(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onNewListOfEvents(ArrayList<HistoryEvent> events) {
        Log.d("HistoryFragment", "onNewListOfEvents");
        adapter.buildModels(events);
    }

    @Override
    public void updateView() {
        progressBar.setVisibility(GONE);
    }

    public interface OnHistoryFragmentInteractionListener {
        void onHistoryEventSelected(int position);
    }

    @Override
    public void onDestroy() {
        eventsDatabaseHandler.destroy();
        super.onDestroy();
    }
}
