package com.example.clientapp.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;
import androidx.recyclerview.widget.RecyclerView;

public class SingleCardViewHolder extends RecyclerView.ViewHolder {
    public TextView newsTitle;
    public TextView newsDescription;
    public ImageView newsImage;

    public SingleCardViewHolder(View itemView) {
        super(itemView);
        newsTitle = itemView.findViewById(R.id.news_title);
        newsDescription = itemView.findViewById(R.id.news_description);
        newsImage = itemView.findViewById(R.id.news_image);
    }

    public void bind(NewsItem item) {
        newsTitle.setText(item.getTitle());
        newsDescription.setText(item.getDescription());
        newsImage.setImageResource(item.getImageResId());
    }
}