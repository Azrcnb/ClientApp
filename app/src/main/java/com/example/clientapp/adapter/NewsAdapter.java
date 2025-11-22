package com.example.clientapp.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clientapp.R;
import com.example.clientapp.model.NewsItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** * NewsAdapter 支持单双列混合排版 + 下拉刷新动画 * 优化：下拉刷新时显示旋转动画（非卡片内容） */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    // ===== 布局类型常量 =====
    public static final int CARD_TYPE_SINGLE = 0; // 单列布局
    public static final int CARD_TYPE_DOUBLE = 1; // 双列布局

    public boolean isLastPage;

    // ===== 新增：卡片删除事件监听器 =====
    public interface OnCardDeleteListener {
        void onCardDeleted(int position);
    }

    private OnCardDeleteListener deleteListener;

    public void setOnCardDeleteListener(OnCardDeleteListener listener) {
        this.deleteListener = listener;
    }

    private List<NewsItem> newsList;
    private Context context;
    private List<Integer> cardTypeList;

    public boolean isLoading = false;

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = new ArrayList<>(newsList);
        Collections.shuffle(this.newsList); // 初始化

        // 初始化 cardTypeList（保持原有逻辑）
        cardTypeList = new ArrayList<>(newsList.size());
        for (int i = 0; i < newsList.size(); i++) {
            cardTypeList.add(Math.random() < 0.3 ? CARD_TYPE_DOUBLE : CARD_TYPE_SINGLE);
        }
        for (int i = 0; i < cardTypeList.size() - 1; i++) {
            if (cardTypeList.get(i) == CARD_TYPE_DOUBLE) {
                cardTypeList.set(i + 1, CARD_TYPE_SINGLE);
            }
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CARD_TYPE_SINGLE) {
            return new SingleViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type1, parent, false)
            );
        } else if (viewType == CARD_TYPE_DOUBLE) {
            return new DoubleViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_type2, parent, false)
            );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        // 原有卡片绑定逻辑（保持不变）
        if (position == getItemCount() - 1 && isLoading) {
            // 上拉加载更多（保持不变）
            holder.newsTitle.setText("正在加载...");
            holder.newsDescription.setText("更多新闻内容即将加载");
            holder.newsImage.setVisibility(View.GONE);
            return;
        }
        if (holder instanceof SingleViewHolder) {
            bindSingleItem((SingleViewHolder) holder, position);
        } else if (holder instanceof DoubleViewHolder) {
            bindDoubleItem((DoubleViewHolder) holder, position);
        }
    }

    private void bindSingleItem(SingleViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.newsTitle.setText(item.getTitle());
        holder.newsDescription.setText(item.getDescription());
        holder.newsImage.setImageResource(item.getImageResId());
        // 长按删除（绑定到当前位置）
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCardDeleted(position);
            }
            return true;
        });
    }

    private void bindDoubleItem(DoubleViewHolder holder, int position) {
        // 获取左项（当前位置）
        NewsItem leftItem = newsList.get(position);
        // 获取右项（当前位置+1，安全检查）
        NewsItem rightItem = (position + 1 < newsList.size()) ? newsList.get(position + 1) : null;

        // 绑定左项
        holder.newsTitleLeft.setText(leftItem.getTitle());
        holder.newsDescriptionLeft.setText(leftItem.getDescription());
        holder.newsImageLeft.setImageResource(leftItem.getImageResId());

        // 绑定右项（如果存在）
        if (rightItem != null) {
            holder.newsTitleRight.setText(rightItem.getTitle());
            holder.newsDescriptionRight.setText(rightItem.getDescription());
            holder.newsImageRight.setImageResource(rightItem.getImageResId());
        } else {
            // 右侧无数据时隐藏视图（安全处理）
            holder.newsTitleRight.setVisibility(View.GONE);
            holder.newsDescriptionRight.setVisibility(View.GONE);
            holder.newsImageRight.setVisibility(View.GONE);
        }

        // 长按删除（绑定到左项位置）
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCardDeleted(position);
            }
            return true;
        });
    }

    public void removeItem(int position) {
        if (position >= 0 && position < newsList.size()) {
            // 检查是否为双列项（通过cardTypeList判断）
            if (cardTypeList.get(position) == CARD_TYPE_DOUBLE) {
                if (position + 1 < newsList.size()) {
                    // 同时删除双列项和右侧项
                    newsList.remove(position + 1);
                    newsList.remove(position);
                    cardTypeList.remove(position + 1);
                    cardTypeList.remove(position);
                    notifyItemRangeRemoved(position, 2);
                } else {
                    // 双列项在末尾（无右侧项）
                    newsList.remove(position);
                    cardTypeList.remove(position);
                    notifyItemRemoved(position);
                }
            } else {
                // 单列项：仅删除当前位置
                newsList.remove(position);
                cardTypeList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void addMoreItems(List<NewsItem> newItems) {
        isLoading = true;
        notifyItemInserted(newsList.size());
        Collections.shuffle(newItems); // 确保新数据随机混合
        new android.os.Handler().postDelayed(() -> {
            // 添加新数据并更新cardTypeList
            int startIndex = newsList.size();
            newsList.addAll(newItems);
            // 为新增数据生成cardType
            for (int i = 0; i < newItems.size(); i++) {
                int type = Math.random() < 0.3 ? CARD_TYPE_DOUBLE : CARD_TYPE_SINGLE;
                cardTypeList.add(type);
            }
            // 修复新增数据的连续性
            for (int i = startIndex; i < cardTypeList.size() - 1; i++) {
                if (cardTypeList.get(i) == CARD_TYPE_DOUBLE) {
                    cardTypeList.set(i + 1, CARD_TYPE_SINGLE);
                }
            }
            notifyItemRangeInserted(startIndex, newItems.size());
            isLoading = false;
        }, 1500);
    }

    // ===== 新增：清空并添加新数据的方法 =====
    public void clearData() {
        newsList.clear();
        cardTypeList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<NewsItem> items) {
        newsList.addAll(items);
        cardTypeList.clear();
        for (int i = 0; i < items.size(); i++) {
            cardTypeList.add(Math.random() < 0.3 ? CARD_TYPE_DOUBLE : CARD_TYPE_SINGLE);
        }
        for (int i = 0; i < cardTypeList.size() - 1; i++) {
            if (cardTypeList.get(i) == CARD_TYPE_DOUBLE) {
                cardTypeList.set(i + 1, CARD_TYPE_SINGLE);
            }
        }
        notifyDataSetChanged();
    }

    // ===== ViewHolder类（保持不变）=====
    public static abstract class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsDescription;
        ImageView newsImage;

        public NewsViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class SingleViewHolder extends NewsViewHolder {
        public SingleViewHolder(View itemView) {
            super(itemView);
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
            super(itemView);
            newsTitleLeft = itemView.findViewById(R.id.news_title_left);
            newsDescriptionLeft = itemView.findViewById(R.id.news_description_left);
            newsImageLeft = itemView.findViewById(R.id.news_image_left);
            newsTitleRight = itemView.findViewById(R.id.news_title_right);
            newsDescriptionRight = itemView.findViewById(R.id.news_description_right);
            newsImageRight = itemView.findViewById(R.id.news_image_right);
        }
    }
}