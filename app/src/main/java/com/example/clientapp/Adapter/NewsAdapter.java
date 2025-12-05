package com.example.clientapp.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    // ===== 布局类型常量 =====
    public static final int CARD_TYPE_SINGLE = 0;
    public static final int CARD_TYPE_DOUBLE = 1;
    public static final int CARD_TYPE_SINGLE_VIDEO = 2;


    private List<NewsItem> newsList;
    private List<NewsCardLayout> cardLayoutList;

    public NewsAdapter(List<NewsItem> newsList, List<NewsCardLayout> cardLayoutList) {
        this.newsList = new ArrayList<>(newsList);
        this.cardLayoutList = new ArrayList<>(cardLayoutList);
    }

    @Override
    public int getItemViewType(int position) {
        int CARD_TYPE = cardLayoutList.get(position).getType();
        int NewIndex = cardLayoutList.get(position).getLeftNewIndex();
        int VideoResId = newsList.get(NewIndex).getVideoResId();
        if (CARD_TYPE == CARD_TYPE_SINGLE && VideoResId != 0) CARD_TYPE = CARD_TYPE_SINGLE_VIDEO;
        return CARD_TYPE;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int CARD_TYPE) {
        if (CARD_TYPE == CARD_TYPE_SINGLE) {
            return new NewsViewHolder.SingleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type1, parent, false));
        } else if (CARD_TYPE == CARD_TYPE_DOUBLE) {
            return new NewsViewHolder.DoubleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type2, parent, false));
        } else if (CARD_TYPE == CARD_TYPE_SINGLE_VIDEO) {
            return new NewsViewHolder.VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type3, parent, false), parent.getContext() );
        }
        throw new IllegalArgumentException("Unsupported card type: " + CARD_TYPE);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder.SingleViewHolder) {
            bindSingleItem((NewsViewHolder.SingleViewHolder) holder, position);
        } else if (holder instanceof NewsViewHolder.DoubleViewHolder) {
            bindDoubleItem((NewsViewHolder.DoubleViewHolder) holder, position);
        } else if (holder instanceof NewsViewHolder.VideoViewHolder) {
            bindVideoItem((NewsViewHolder.VideoViewHolder) holder, position);
        }
    }

    // 新增 addMoreData 方法（用于加载更多）
    public void addMoreData(List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        int startIndex = newsList.size();
        newsList.addAll(newData);
        cardLayoutList.addAll(newCardTypeList);
        notifyItemRangeInserted(startIndex, newData.size());
    }

    // 新增 refreshData 方法（用于刷新）
    public void refreshData(List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        this.newsList = new ArrayList<>(newData);
        this.cardLayoutList = new ArrayList<>(newCardTypeList);
        notifyDataSetChanged();
    }

    // 新增 removeNewsData 方法（用于删除，不修改数据，只更新UI）
    // ✅ 新增 removeNewsData 方法 - 只更新受影响的卡片
    public void removeNewsData(int position, List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        // 2. 获取当前卡片类型
        int cardType = this.cardLayoutList.get(position).getType();

        // 1. 先用新数据替换当前数据
         this.newsList = new ArrayList<>(newData);
         this.cardLayoutList = new ArrayList<>(newCardTypeList);

        // 3. 根据卡片类型和删除情况决定更新方式
        if (cardType == CARD_TYPE_SINGLE) {
            // 单列卡片：直接删除该位置
            notifyItemRemoved(position);
        } else if (cardType == CARD_TYPE_DOUBLE) {
            // 双列卡片：只更新该位置（从双列变为单列）
            notifyItemChanged(position);
        }
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

    private void bindVideoItem(NewsViewHolder.VideoViewHolder holder, int position) {
        int NewIndex = cardLayoutList.get(position).getLeftNewIndex();
        NewsItem item = newsList.get(NewIndex);
        holder.newsTitle.setText(item.getTitle());
        holder.newsDescription.setText(item.getDescription());
        //加载视频资源
        holder.loadVideoRes(item);
    }

    @Override
    public int getItemCount() {
        return cardLayoutList.size(); // ✅ 关键：返回卡片数量（cardLayoutList的长度）
    }
}