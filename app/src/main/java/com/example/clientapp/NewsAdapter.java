package com.example.clientapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    // ===== 布局类型常量 =====
    public static final int CARD_TYPE_SINGLE = 0;
    public static final int CARD_TYPE_DOUBLE = 1;


    private List<NewsItem> newsList;
    private List<NewsCardLayout> cardLayoutList;
    private Context context;

    public NewsAdapter(Context context, List<NewsItem> newsList, List<NewsCardLayout> cardLayoutList) {
        this.context = context;
        this.newsList = new ArrayList<>(newsList);
        this.cardLayoutList = new ArrayList<>(cardLayoutList);
    }

    @Override
    public int getItemViewType(int position) {
        return cardLayoutList.get(position).getType();
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CARD_TYPE_SINGLE) {
            return new NewsViewHolder.SingleViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type1, parent, false)
            );
        } else {
            return new NewsViewHolder.DoubleViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type2, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder.SingleViewHolder) {
            bindSingleItem((NewsViewHolder.SingleViewHolder) holder, position);
        } else if (holder instanceof NewsViewHolder.DoubleViewHolder) {
            bindDoubleItem((NewsViewHolder.DoubleViewHolder) holder, position);
        }
    }

    public void updateData(List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        this.newsList = newData;
        this.cardLayoutList = newCardTypeList;
        notifyDataSetChanged();
    }

    private void bindSingleItem(NewsViewHolder.SingleViewHolder holder, int position) {
        int NewIndex = cardLayoutList.get(position).getLeftNewIndex();
        NewsItem item = newsList.get(NewIndex);
        holder.newsTitle.setText(item.getTitle());
        holder.newsDescription.setText(item.getDescription());
        holder.newsImage.setImageResource(item.getImageResId());
    }

    private void bindDoubleItem(NewsViewHolder.DoubleViewHolder holder, int position) {
        int leftNewIndex = cardLayoutList.get(position).getLeftNewIndex();
        int rightNewIndex = cardLayoutList.get(position).getRightNewIndex();

        NewsItem leftItem = newsList.get(leftNewIndex);
        NewsItem rightItem = newsList.get(rightNewIndex);

        // 绑定左项
        holder.newsTitleLeft.setText(leftItem.getTitle());
        holder.newsDescriptionLeft.setText(leftItem.getDescription());
        holder.newsImageLeft.setImageResource(leftItem.getImageResId());
        // 绑定右项
        holder.newsTitleRight.setText(rightItem.getTitle());
        holder.newsDescriptionRight.setText(rightItem.getDescription());
        holder.newsImageRight.setImageResource(rightItem.getImageResId());
    }


    @Override
    public int getItemCount() {
        return cardLayoutList.size(); // ✅ 关键：返回卡片数量（cardLayoutList的长度）
    }
}