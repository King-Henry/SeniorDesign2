package com.wiita.smartlockapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class SummaryListFragment extends Fragment
        implements ImagesDatabaseHandler.ImageLoaderListener,
        YouTubePlayer.OnInitializedListener
{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button unlockButton;
    private Button lockButton;
    private ImageView liveFeedImage;
    private ProgressBar progressBar;
    private FrameLayout liveFeedContainer;

    private ImagesDatabaseHandler imagesDatabaseHandler;
    private final static int REQUEST_COARSE_LOCATION = 10;
    public final static String YOUTUBE_API_KEY = "AIzaSyAkLJdTYa8TMRU7Td9mblFfdh7iQgRFYF0";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSummaryFragmentInteractionListener mListener;
    private final String YOUTUBE_FRAGMENT_TAG = "YOUTUBE_FRAGMENT_TAG";

    public SummaryListFragment() {
        // Required empty public constructor
    }

    public static SummaryListFragment newInstance(String param1, String param2) {
        SummaryListFragment fragment = new SummaryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary,container,false);

        liveFeedContainer = (FrameLayout)rootView.findViewById(R.id.live_feed_container);

        YouTubePlayerSupportFragment fragment;
        if(savedInstanceState == null) {
            Log.d("onCreateView", "adding YouTube fragment");
            fragment = new YouTubePlayerSupportFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.youtube_fragment, fragment,YOUTUBE_FRAGMENT_TAG).commit();
            fragment.initialize(YOUTUBE_API_KEY, this);
        } else {
            fragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
            fragment.initialize(YOUTUBE_API_KEY, this);
        }

        imagesDatabaseHandler = new ImagesDatabaseHandler(this);

        liveFeedImage = (ImageView)rootView.findViewById(R.id.live_feed_imageview);
        unlockButton = (Button)rootView.findViewById(R.id.summary_fragment_unlock_button);
        lockButton = (Button)rootView.findViewById(R.id.summary_fragment_lock_button);
        progressBar = (ProgressBar)rootView.findViewById(R.id.live_feed_loader);
        checkLocationPermission();
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesDatabaseHandler.sendUnlockCommand();
            }
        });
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesDatabaseHandler.sendLockCommand();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (imagesDatabaseHandler != null) {
            // So Glide caches pictures and it seems that if the same url is used for a new picture, Glide will not reload the image
            // So everytime the fragment appears I clear the cache...
            // Probably not the best wait to do this,but I'm sure future Tim will learn lol
            imagesDatabaseHandler.clearImageCache(getActivity());
        }
    }

    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //TODO: DO THIS
            }
            else{
                Log.d("onRequestPermissions...", "onRequestPermissionsResult: Permission not granted");
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("jAZlQLSXCIc");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onImageReady(String url) {
        Glide.with(this)
                .load(url)
                .into(liveFeedImage);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void setLoadingState() {
        Log.d("setLoadingState,", "setLoadingState: making loader visible");
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSummaryFragmentInteractionListener) {
            mListener = (OnSummaryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSummaryFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSummaryFragmentInteraction(Uri uri);
    }
}
