package com.wiita.smartlockapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.koushikdutta.ion.Ion;

public class SummaryFragment extends Fragment
        implements ImagesDatabaseHandler.ImageLoaderListener,
        YouTubePlayer.OnInitializedListener
{

    private Button unlockButton;
    private Button lockButton;
    private ImageView liveFeedImage;
    private ProgressBar progressBar;
    private FrameLayout liveFeedContainer;

    private CommandDatabaseHandler commandDatabaseHandler;

    private ImagesDatabaseHandler imagesDatabaseHandler;
    private final static int REQUEST_COARSE_LOCATION = 10;
    public final static String YOUTUBE_API_KEY = "AIzaSyAkLJdTYa8TMRU7Td9mblFfdh7iQgRFYF0";

    private OnSummaryFragmentInteractionListener mListener;
    private final String YOUTUBE_FRAGMENT_TAG = "YOUTUBE_FRAGMENT_TAG";

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary,container,false);
        liveFeedContainer = (FrameLayout)rootView.findViewById(R.id.live_feed_container);

        commandDatabaseHandler = new CommandDatabaseHandler();

        liveFeedImage = (ImageView)rootView.findViewById(R.id.live_feed_imageview);
        unlockButton = (Button)rootView.findViewById(R.id.summary_fragment_unlock_button);
        lockButton = (Button)rootView.findViewById(R.id.summary_fragment_lock_button);
        progressBar = (ProgressBar)rootView.findViewById(R.id.live_feed_loader);
        checkLocationPermission();
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandDatabaseHandler.sendUnlockCommand();
            }
        });
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commandDatabaseHandler.sendLockCommand();
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
    public void onResume() {
        super.onResume();
        handleLiveFeedType();
    }

    private void handleLiveFeedType(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String liveFeedType = prefs.getString("live_feed_type","");
        if(liveFeedType.equals("1") || liveFeedType.isEmpty()){
            if(imagesDatabaseHandler == null){
                imagesDatabaseHandler = new ImagesDatabaseHandler(this);
            }
            FragmentManager manager = getChildFragmentManager();
            YouTubePlayerSupportFragment fragment = (YouTubePlayerSupportFragment)manager.findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
            if(fragment == null){
                //DO NOTHING...I think
            } else{
                manager.beginTransaction().remove(fragment).commit();
                liveFeedImage.setVisibility(View.VISIBLE);
            }
        }else{
            if(liveFeedImage.getVisibility() == View.VISIBLE){
                liveFeedImage.setVisibility(View.GONE);
                if(imagesDatabaseHandler != null){
                    imagesDatabaseHandler.destroy();
                    imagesDatabaseHandler = null;
                }
                FragmentManager manager = getChildFragmentManager();
                YouTubePlayerSupportFragment fragment = (YouTubePlayerSupportFragment)manager.findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
                if(fragment == null){
                    fragment = new YouTubePlayerSupportFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(R.id.youtube_fragment, fragment,YOUTUBE_FRAGMENT_TAG).commit();
                    fragment.initialize(YOUTUBE_API_KEY, this);
                } else{
                    fragment.initialize(YOUTUBE_API_KEY,this);
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        if(imagesDatabaseHandler != null){
            imagesDatabaseHandler.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onImageReady(Image image) {
        Glide.with(this)
                .asGif()
                .load(image.url)
                .into(liveFeedImage);
        progressBar.setVisibility(View.GONE);
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
