package com.example.clientapp.ViewModel;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clientapp.Adapter.NewsViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 专门处理卡片曝光事件跟踪的类
 * 负责检测卡片在RecyclerView中的可见性，并记录相应的曝光事件
 */
public class ExposureTracker {
    private final Map<Integer, Boolean> cardExposedStatus = new HashMap<>();
    private final Map<Integer, Boolean> cardHalfExposedStatus = new HashMap<>();
    private final Map<Integer, Boolean> cardFullyExposedStatus = new HashMap<>();
    private final List<String> exposureEvents = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


    /**
     * 检查RecyclerView中可见卡片的曝光状态
     * @param recyclerView RecyclerView实例
     * @param firstVisibleItem 第一个可见的item位置
     * @param lastVisibleItem 最后一个可见的item位置
     * @param totalItemCount 总item数量
     */
    public void checkExposure(RecyclerView recyclerView, int firstVisibleItem, int lastVisibleItem, int totalItemCount) {
        // 如果RecyclerView没有布局管理器，直接返回
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        // 获取RecyclerView在屏幕上的位置
        int[] recyclerViewLocation = new int[2];
        recyclerView.getLocationOnScreen(recyclerViewLocation);
        int recyclerViewTop = recyclerViewLocation[1];
        int recyclerViewBottom = recyclerViewLocation[1] + recyclerView.getHeight();

        // 检查每个可见的卡片
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder == null) continue;

            if (i >= totalItemCount) break;

            int childIndex = i - firstVisibleItem;
            if (childIndex < 0 || childIndex >= recyclerView.getChildCount()) {
                continue;
            }

            View view = recyclerView.getChildAt(childIndex);
            if (view == null) {
                continue;
            }

            // 获取卡片在屏幕上的位置
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int viewTop = location[1];
            int viewBottom = viewTop + view.getHeight();

            // 计算可见区域
            int visibleTop = Math.max(viewTop, recyclerViewTop);
            int visibleBottom = Math.min(viewBottom, recyclerViewBottom);
            int visibleHeight = Math.max(0, visibleBottom - visibleTop);


            // 计算可见比例
            float visibilityRatio = (float) visibleHeight / view.getHeight();

            // 检查并记录曝光事件
            if (visibilityRatio > 0.0f && Boolean.FALSE.equals(cardExposedStatus.getOrDefault(i, false))) {
                String event = String.format("卡片露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardExposedStatus.put(i, true);
            }

            if (visibilityRatio >= 0.5f && Boolean.FALSE.equals(cardHalfExposedStatus.getOrDefault(i, false))) {
                String event = String.format("卡片露出超过50%%: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardHalfExposedStatus.put(i, true);
            }

            if (visibilityRatio >= 0.9f && Boolean.FALSE.equals(cardFullyExposedStatus.getOrDefault(i, false))) {
                String event = String.format("卡片完整露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardFullyExposedStatus.put(i, true);
            }

            if (visibilityRatio <= 0.2f && Boolean.TRUE.equals(cardFullyExposedStatus.getOrDefault(i, false))) {
                String event = String.format("卡片消失: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardExposedStatus.put(i, false);
                cardHalfExposedStatus.put(i, false);
                cardFullyExposedStatus.put(i, false);
            }

            // ✅ 关键修改3：只有视频卡片才操作视频（单独处理）
            if (viewHolder instanceof NewsViewHolder.VideoViewHolder) {
                NewsViewHolder.VideoViewHolder videoHolder = (NewsViewHolder.VideoViewHolder) viewHolder;
                if (visibilityRatio >= 0.9f) {
                    videoHolder.playVideo(); // 卡片露出50%以上才播放
                } else if (visibilityRatio <= 0.5f) {
                    videoHolder.pauseVideo(); // 卡片几乎消失时暂停
                }
            }
        }
    }

    /**
     * 获取所有曝光事件
     * @return 曝光事件列表
     */
    public List<String> getExposureEvents() {
        return new ArrayList<>(exposureEvents);
    }

}