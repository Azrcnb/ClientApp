package com.example.clientapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;
import androidx.recyclerview.widget.RecyclerView;

public class VideoCardViewHolder extends RecyclerView.ViewHolder {
    public TextView newsTitle;
    public TextView newsDescription;
    public VideoView newsVideo;
    public ImageView videoCover;
    private boolean isPlaying = false;
    private final Context context;

    public VideoCardViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        newsTitle = itemView.findViewById(R.id.news_title);
        newsDescription = itemView.findViewById(R.id.news_description);
        newsVideo = itemView.findViewById(R.id.news_video);
        videoCover = itemView.findViewById(R.id.video_cover);
        newsVideo.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    public void bind(NewsItem item) {
        newsTitle.setText(item.getTitle());
        newsDescription.setText(item.getDescription());
        loadVideoRes(item);
    }

    public void loadVideoRes(NewsItem newsItem) {
        if (newsItem.getVideoResId() == 0) return;

        int coverImageResId = newsItem.getImageResId();
        videoCover.setImageResource(coverImageResId);
        Uri videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + newsItem.getVideoResId());
        newsVideo.setVideoURI(videoUri);
    }

    public void playVideo() {
        if (!isPlaying) {
            newsVideo.start();
            isPlaying = true;
            videoCover.setVisibility(View.GONE);
        }
    }

    public void pauseVideo() {
        if (isPlaying) {
            newsVideo.pause();
            isPlaying = false;
            videoCover.setVisibility(View.VISIBLE);
        }
    }
}