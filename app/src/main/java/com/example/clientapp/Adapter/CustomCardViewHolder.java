package com.example.clientapp.Adapter;

import android.view.View;
import android.widget.TextView;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;
import androidx.recyclerview.widget.RecyclerView;

public class CustomCardViewHolder extends RecyclerView.ViewHolder {
    public TextView customCardTitle;
    public TextView customCardDescription;

    public CustomCardViewHolder(View itemView) {
        super(itemView);
        customCardTitle = itemView.findViewById(R.id.custom_card_title);
        customCardDescription = itemView.findViewById(R.id.custom_card_description);
    }

    public void bind(NewsItem item) {
        customCardTitle.setText(item.getTitle());
        customCardDescription.setText(item.getDescription());
    }
}