package com.example.clientapp.Adapter;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;

/** * 修复关键点： * 1. 必须显式调用 RecyclerView.ViewHolder 的构造 (super(itemView)) * 2. 使用全限定类名避免IDE混淆 */
public class NewsViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
    // 基础控件
    public TextView newsTitle;
    public TextView newsDescription;
    public ImageView newsImage;

    public NewsViewHolder(View itemView) {
        super(itemView); // 必须调用父类构造
    }

    public static class SingleViewHolder extends NewsViewHolder {
        public SingleViewHolder(View itemView) {
            super(itemView); // 正确调用父类构造
            newsTitle = itemView.findViewById(R.id.news_title);
            newsDescription = itemView.findViewById(R.id.news_description);
            newsImage = itemView.findViewById(R.id.news_image);
        }
    }

    public static class DoubleViewHolder extends NewsViewHolder {
        TextView newsTitleLeft;
        TextView newsDescriptionLeft;
        ImageView newsImageLeft;
        TextView newsTitleRight;
        TextView newsDescriptionRight;
        ImageView newsImageRight;

        public DoubleViewHolder(View itemView) {
            super(itemView); // 正确调用父类构造
            newsTitleLeft = itemView.findViewById(R.id.news_title_left);
            newsDescriptionLeft = itemView.findViewById(R.id.news_description_left);
            newsImageLeft = itemView.findViewById(R.id.news_image_left);
            newsTitleRight = itemView.findViewById(R.id.news_title_right);
            newsDescriptionRight = itemView.findViewById(R.id.news_description_right);
            newsImageRight = itemView.findViewById(R.id.news_image_right);
        }
    }

    public static class VideoViewHolder extends NewsViewHolder  { // 修改继承关系
        ImageView videoCover; // 新增：用于封面
        VideoView newsVideo;
        Uri videoUri;
        boolean isPlaying = false;
        Context context;


        public VideoViewHolder(View itemView,Context context) {
            super(itemView);
            this.context = context;
            newsTitle = itemView.findViewById(R.id.news_title);
            newsDescription = itemView.findViewById(R.id.news_description);
            newsVideo = itemView.findViewById(R.id.news_video);
            videoCover = itemView.findViewById(R.id.video_cover);



            // 初始化VideoView
            newsVideo.setOnPreparedListener(mp -> mp.setLooping(true));

        }

        public void loadVideoRes(NewsItem newsItem) {
            if (newsItem.getVideoResId() == 0) return;
            // 设置封面：使用新闻的图片作为视频封面
            int coverImageResId = newsItem.getImageResId();
            videoCover.setImageResource(coverImageResId);
            this.videoUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + newsItem.getVideoResId());
            newsVideo.setVideoURI(videoUri);
            // 设置视频封面
            // newsVideo.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + newsItem.getImageResId()));
        }

        public void playVideo() {
            if (!isPlaying) {
                newsVideo.start();
                isPlaying = true;
                // 关键修改：视频开始播放后隐藏封面
                videoCover.setVisibility(View.GONE);
            }
        }

        public void pauseVideo() {
            if (isPlaying) {
                newsVideo.pause();
                isPlaying = false;
                // 关键修改：视频暂停时重新显示封面
                videoCover.setVisibility(View.VISIBLE);
            }
        }
    }
}