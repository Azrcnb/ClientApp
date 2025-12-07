package com.example.clientapp.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;
import androidx.recyclerview.widget.RecyclerView;

public class DoubleCardViewHolder extends RecyclerView.ViewHolder {
    public TextView newsTitleLeft;
    public TextView newsDescriptionLeft;
    public ImageView newsImageLeft;
    public TextView newsTitleRight;
    public TextView newsDescriptionRight;
    public ImageView newsImageRight;

    public DoubleCardViewHolder(View itemView) {
        super(itemView);
        newsTitleLeft = itemView.findViewById(R.id.news_title_left);
        newsDescriptionLeft = itemView.findViewById(R.id.news_description_left);
        newsImageLeft = itemView.findViewById(R.id.news_image_left);
        newsTitleRight = itemView.findViewById(R.id.news_title_right);
        newsDescriptionRight = itemView.findViewById(R.id.news_description_right);
        newsImageRight = itemView.findViewById(R.id.news_image_right);
    }

    public void bind(NewsItem leftItem, NewsItem rightItem) {
        newsTitleLeft.setText(leftItem.getTitle());
        newsDescriptionLeft.setText(leftItem.getDescription());
        newsImageLeft.setImageResource(leftItem.getImageResId());

        newsTitleRight.setText(rightItem.getTitle());
        newsDescriptionRight.setText(rightItem.getDescription());
        newsImageRight.setImageResource(rightItem.getImageResId());
    }
}