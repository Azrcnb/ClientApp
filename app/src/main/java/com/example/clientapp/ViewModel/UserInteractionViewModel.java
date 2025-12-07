package com.example.clientapp.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.Adapter.NewsAdapter;
import com.example.clientapp.Repository.Repository;
import com.example.clientapp.Activity.TestToolActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/** * 负责管理所有用户交互监听的类，使MainActivity保持简洁 */
public class UserInteractionViewModel {
    private final Context context;
    private final RecyclerView recyclerView;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final NewsAdapter newsAdapter;
    private final Repository repository;
    private final TabLayout tabLayout;
    private final ExposureTracker exposureTracker;
    private long lastExposureCheckTime = 0;
    private static final long EXPOSURE_CHECK_INTERVAL = 500;

    public UserInteractionViewModel(Context context, RecyclerView recyclerView,
                                    SwipeRefreshLayout swipeRefreshLayout,
                                    NewsAdapter newsAdapter,
                                    Repository repository,
                                    TabLayout tabLayout) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.newsAdapter = newsAdapter;
        this.repository = repository;
        this.tabLayout = tabLayout;
        this.exposureTracker = new ExposureTracker();
        setupListeners();
    }

    private void setupListeners() {
        setupTabListener();
        setupScrollListener();
        setupSwipeRefreshListener();
        setupDualColumnTouchListener();
    }

    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                assert tab.getText() != null;
                String tabText = tab.getText().toString();
                // 点击"关注"、"推荐"、"热搜"时刷新新闻
                if ("关注".equals(tabText) || "推荐".equals(tabText) || "热搜".equals(tabText)) {
                    repository.refreshNews();
                }
                // "测试"标签保持不变
                else if ("测试".equals(tabText)) {
                    Intent intent = new Intent(context, TestToolActivity.class);
                    intent.putStringArrayListExtra("EXPOSURE_EVENTS", new ArrayList<>(exposureTracker.getExposureEvents()));
                    context.startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = newsAdapter.getItemCount();

                // 检查曝光事件
                if (System.currentTimeMillis() - lastExposureCheckTime > EXPOSURE_CHECK_INTERVAL) {
                    exposureTracker.checkExposure(recyclerView, firstVisibleItem, lastVisibleItem, totalItemCount);
                    lastExposureCheckTime = System.currentTimeMillis();
                }

                // 加载更多数据
                if (repository.getIsLoading().getValue() != null && !repository.getIsLoading().getValue() &&
                        lastVisibleItem >= totalItemCount - 2) {
                    repository.loadMoreNews();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                        int totalItemCount = newsAdapter.getItemCount();
                        exposureTracker.checkExposure(recyclerView, firstVisibleItem, lastVisibleItem, totalItemCount);
                    }
                }
            }
        });
    }

    private void setupSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(repository::refreshNews);
    }

    private void setupDualColumnTouchListener() {
        recyclerView.addOnItemTouchListener(
                new TouchListener(
                        context,
                        recyclerView,
                        (Position, isLeftColumn) -> new AlertDialog.Builder(context)
                                .setTitle("删除新闻")
                                .setMessage("确定要删除这条新闻吗？")
                                .setPositiveButton("删除", (dialog, which) -> {
                                    // 移除新闻
                                    repository.removeNews(Position, isLeftColumn);
                                    List<NewsItem> updatedNewsData = repository.getCurrentNewsData();
                                    List<NewsCardLayout> updatedCardLayoutData = repository.getCurrentCardLayoutData();
                                    newsAdapter.removeNewsData(Position, updatedNewsData, updatedCardLayoutData);
                                    Toast.makeText(context, "新闻已删除", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("取消", null)
                                .show()
                )
        );
    }
}