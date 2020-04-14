package com.zalocoders.videoup2.Views.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zalocoders.videoup2.R;
import com.zalocoders.videoup2.Views.Adapter.VideoAdapter;
import com.zalocoders.videoup2.Views.models.UploadItem;
import com.zalocoders.videoup2.Views.viewmodel.VideoFeedViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoFeedFragment extends Fragment{

    private DatabaseReference mdatabaseRef;
    private RecyclerView videRecyclerView;
    View v;
    private VideoAdapter adapter;
    private List<UploadItem> videoList;
    private ProgressBar progress;
    private Context context;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
          v  = inflater.inflate(R.layout.video_feed_fragment, container, false);
        assert container != null;
        context = container.getContext();

        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        videoList = new ArrayList<>();

        setUpRecyclerView();

        progress = v.findViewById(R.id.progress);


         return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VideoFeedViewModel mViewModel = new ViewModelProvider(this).get(VideoFeedViewModel.class);

    }


    //preparing recyclerview to handle data
    private void setUpRecyclerView(){

        videRecyclerView = v.findViewById(R.id.videRecyclerView);
        adapter = new VideoAdapter(context,videoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        videRecyclerView.setLayoutManager(layoutManager);
        videRecyclerView.setAdapter(adapter);


        fetchVideos();
      // scrollingFunctionality();

    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayerStandard.releaseAllVideos();
        JCVideoPlayer.releaseAllVideos();
    }



    private void fetchVideos(){

        // creating an event listener for any change in the database
        ValueEventListener mDBListener = mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear old data
                videoList.clear();

                for (DataSnapshot video : dataSnapshot.getChildren()) {
                    UploadItem item = video.getValue(UploadItem.class);
                    videoList.add(item);
                    progress.setVisibility(View.GONE);

                }

                //updating the changed data
                reverse(videoList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progress.setVisibility(View.GONE);
            }
        });

    }


    //rearrange received data in most current post
    private void reverse(List<UploadItem> items){
        Collections.reverse(items);
    }


    //handling scroll listeners on the recycler view
    private void scrollingFunctionality(){


        videRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                JCVideoPlayerStandard.releaseAllVideos();
                JCVideoPlayer.releaseAllVideos();
            }
        });

    }

}
