package com.example.clientapp;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * 修复关键点：
 * 1. 必须显式调用 RecyclerView.ViewHolder 的构造 (super(itemView))
 * 2. 使用全限定类名避免IDE混淆
 */
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
}