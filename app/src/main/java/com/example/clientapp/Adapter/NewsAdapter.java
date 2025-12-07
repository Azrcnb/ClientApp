package com.example.clientapp.Adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        int newIndex = cardLayoutList.get(position).getLeftNewIndex();
        int videoResId = newsList.get(newIndex).getVideoResId();
        if (CARD_TYPE == CARD_TYPE_SINGLE && videoResId != 0) {
            CARD_TYPE = CARD_TYPE_SINGLE_VIDEO;
        }
        return CARD_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int CARD_TYPE) {
        return CardTypeRegistry.getInstance().createViewHolder(CARD_TYPE, parent, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int newIndex = cardLayoutList.get(position).getLeftNewIndex();
        NewsItem item = newsList.get(newIndex);

        // 获取rightItem
        NewsItem rightItem = null;
        if (holder.getItemViewType() == CARD_TYPE_DOUBLE) {
            int rightNewIndex = cardLayoutList.get(position).getRightNewIndex();
            rightItem = newsList.get(rightNewIndex);
        }

        // 委托给CardTypeRegistry绑定
        CardTypeRegistry.getInstance().bindViewHolder(holder, item, rightItem);
    }

    @Override
    public int getItemCount() {
        return cardLayoutList.size();
    }

    // 新增 addMoreData 方法（用于加载更多）
    public void addMoreData(List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        int startIndex = newsList.size();
        newsList.addAll(newData);
        cardLayoutList.addAll(newCardTypeList);
        notifyItemRangeInserted(startIndex, newData.size());
    }

    // 新增 refreshData 方法（用于刷新）
    @SuppressLint("NotifyDataSetChanged")
    public void refreshData(List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        this.newsList = new ArrayList<>(newData);
        this.cardLayoutList = new ArrayList<>(newCardTypeList);
        notifyDataSetChanged();
    }

    // 新增 removeNewsData 方法（用于删除，不修改数据，只更新UI）
    public void removeNewsData(int position, List<NewsItem> newData, List<NewsCardLayout> newCardTypeList) {
        int cardType = this.cardLayoutList.get(position).getType();
        this.newsList = new ArrayList<>(newData);
        this.cardLayoutList = new ArrayList<>(newCardTypeList);

        if (cardType == CARD_TYPE_SINGLE) {
            notifyItemRemoved(position);
        } else if (cardType == CARD_TYPE_DOUBLE) {
            notifyItemChanged(position);
        }
    }
}