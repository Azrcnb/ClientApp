package com.example.clientapp.ListenerManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.widget.Toast;

import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.NewsAdapter;
import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewModelListener {
    private final NewsViewModel newsViewModel;
    private final RecyclerView.Adapter newsAdapter;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final Context context;

    public ViewModelListener(NewsViewModel newsViewModel, RecyclerView.Adapter newsAdapter,
                             SwipeRefreshLayout swipeRefreshLayout, Context context) {
        this.newsViewModel = newsViewModel;
        this.newsAdapter = newsAdapter;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.context = context;

        setupObservers();
    }

    private void setupObservers() {
        // 监听新闻数据
        newsViewModel.getUpdateType().observeForever(updateType -> {
            if ("loadMore".equals(updateType)) {
                List<NewsItem> newsItems = newsViewModel.getNewsData().getValue();
                List<NewsCardLayout> newsCardLayoutData = newsViewModel.getCardLayoutData().getValue();
                if (newsItems != null && newsCardLayoutData != null) {
                    ((NewsAdapter) newsAdapter).addMoreData(new ArrayList<>(newsItems), new ArrayList<>(newsCardLayoutData));
                }
            } else if ("refresh".equals(updateType)) {
                List<NewsItem> newsItems = newsViewModel.getNewsData().getValue();
                List<NewsCardLayout> newsCardLayoutData = newsViewModel.getCardLayoutData().getValue();
                if (newsItems != null && newsCardLayoutData != null) {
                    ((NewsAdapter) newsAdapter).refreshData(new ArrayList<>(newsItems), new ArrayList<>(newsCardLayoutData));
                }
            }
        });

        // 监听加载状态
        newsViewModel.getIsLoading().observeForever(isLoading -> {
            if (isLoading != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });

        // 监听错误
        newsViewModel.getError().observeForever(errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}