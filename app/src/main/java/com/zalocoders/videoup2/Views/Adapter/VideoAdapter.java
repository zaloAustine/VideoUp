package com.zalocoders.videoup2.Views.Adapter;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.zalocoders.videoup2.R;
import com.zalocoders.videoup2.Views.models.UploadItem;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private final Context mcontext;
    private List<UploadItem> itemList;
    private UploadItem singleItem;




    public VideoAdapter(Context mcontext, List<UploadItem> itemList) {
        this.mcontext = mcontext;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
      View  v = layoutInflater.inflate(R.layout.video_item,parent,false);
        return new VideoHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {

        singleItem = itemList.get(position);
        holder.names.setText(singleItem.getName());
        holder.description.setText(singleItem.getDecs());

        RequestOptions requestOptions = new RequestOptions();
        if(requestOptions.isMemoryCacheable()){
            holder.videoPalyer.startButton.performClick();
            Glide.with(mcontext).setDefaultRequestOptions(requestOptions).load(singleItem.getUrl()).diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.videoPalyer.thumbImageView);

        }else {

              Glide.with(mcontext).load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640").diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.videoPalyer.thumbImageView);

        }

        getVideo(singleItem.getUrl(),holder.videoPalyer);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder{

        private final TextView names,description;
        private final JCVideoPlayerStandard videoPalyer;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            names = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            videoPalyer = itemView.findViewById(R.id.videoPalyer);

        }
    }


    public void getVideo(final String url, JCVideoPlayerStandard video){

        video.setUp(url,JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN);


        video.battery_level.setVisibility(View.GONE);
        video.batteryTimeLayout.setVisibility(View.GONE);
        video.backButton.setVisibility(View.GONE);

        //auto play
         video.startButton.performClick();



        video.fullscreenButton.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(mcontext).setDefaultRequestOptions(requestOptions).load(singleItem.getUrl()) .diskCacheStrategy(DiskCacheStrategy.DATA).into(video.thumbImageView);


        }


}



