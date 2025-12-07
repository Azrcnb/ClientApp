package com.example.clientapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.R;
import java.util.HashMap;
import java.util.Map;

public class CardTypeRegistry {
    private static CardTypeRegistry instance;
    private final Map<Integer, CardTypeFactory> factories = new HashMap<>();
    private final Map<Integer, ViewHolderBinder> binders = new HashMap<>();


    private CardTypeRegistry() {
        // 注册卡片类型工厂
        registerCardType(CARD_TYPE_SINGLE, (parent, context) -> new SingleCardViewHolder(LayoutInflater.from(context).inflate(R.layout.card_type1, parent, false)));
        registerCardType(CARD_TYPE_DOUBLE, (parent, context) -> new DoubleCardViewHolder(LayoutInflater.from(context).inflate(R.layout.card_type2, parent, false)));
        registerCardType(CARD_TYPE_SINGLE_VIDEO, (parent, context) -> new VideoCardViewHolder(LayoutInflater.from(context).inflate(R.layout.card_type3, parent, false), context));
        registerCardType(CARD_TYPE_CUSTOM, (parent, context) -> new CustomCardViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_card_layout, parent, false)));

        // 注册绑定逻辑
        registerBinder(CARD_TYPE_SINGLE, (holder, item, rightItem) ->
                ((SingleCardViewHolder) holder).bind(item));

        registerBinder(CARD_TYPE_SINGLE_VIDEO, (holder, item, rightItem) ->
                ((VideoCardViewHolder) holder).bind(item));

        registerBinder(CARD_TYPE_CUSTOM, (holder, item, rightItem) ->
                ((CustomCardViewHolder) holder).bind(item));
        registerBinder(CARD_TYPE_DOUBLE, (holder, item, rightItem) -> {
            // 双列需要两个NewsItem，rightItem 会是实际的第二个新闻项
            ((DoubleCardViewHolder) holder).bind(item, rightItem);
        });
    }

    public static CardTypeRegistry getInstance() {
        if (instance == null) {
            instance = new CardTypeRegistry();
        }
        return instance;
    }

    public void registerCardType(int typeId, CardTypeFactory factory) {
        factories.put(typeId, factory);
    }

    public void registerBinder(int typeId, ViewHolderBinder binder) {
        binders.put(typeId, binder);
    }

    public RecyclerView.ViewHolder createViewHolder(int typeId, ViewGroup parent, Context context) {
        CardTypeFactory factory = factories.get(typeId);
        if (factory != null) {
            return factory.createViewHolder(parent, context);
        }
        return new SingleCardViewHolder(LayoutInflater.from(context).inflate(R.layout.card_type1, parent, false));
    }

    // 核心修改：现在接受两个NewsItem，第二个参数在单列时为null
    public void bindViewHolder(RecyclerView.ViewHolder holder, NewsItem item, NewsItem rightItem) {
        ViewHolderBinder binder = binders.get(holder.getItemViewType());
        if (binder != null) {
            binder.bind(holder, item, rightItem);
        } else {
            // 安全回退
            ((SingleCardViewHolder) holder).bind(item);
        }
    }

    public interface CardTypeFactory {
        RecyclerView.ViewHolder createViewHolder(ViewGroup parent, Context context);
    }

    public interface ViewHolderBinder {
        void bind(RecyclerView.ViewHolder holder, NewsItem item, NewsItem rightItem);
    }

    // 卡片类型常量
    public static final int CARD_TYPE_SINGLE = 0;
    public static final int CARD_TYPE_DOUBLE = 1;
    public static final int CARD_TYPE_SINGLE_VIDEO = 2;
    public static final int CARD_TYPE_CUSTOM = 3;
}