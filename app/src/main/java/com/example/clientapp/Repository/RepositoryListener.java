package com.example.clientapp.Repository;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.widget.Toast;

import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.Adapter.NewsAdapter;
import com.example.clientapp.Model.NewsCardLayout;

import java.util.ArrayList;
import java.util.List;

public class RepositoryListener {
    private final Repository repository;
    private final NewsAdapter newsAdapter;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final Context context;

    public RepositoryListener(Repository repository, NewsAdapter newsAdapter,
                              SwipeRefreshLayout swipeRefreshLayout, Context context) {
        this.repository = repository;
        this.newsAdapter = newsAdapter;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.context = context;

        setupObservers();
    }

    private void setupObservers() {
        // 监听新闻数据
        repository.getUpdateType().observeForever(updateType -> {
            if ("loadMore".equals(updateType)) {
                List<NewsItem> newsItems = repository.getNewsData().getValue();
                List<NewsCardLayout> newsCardLayoutData = repository.getCardLayoutData().getValue();
                if (newsItems != null && newsCardLayoutData != null) {
                    newsAdapter.addMoreData(new ArrayList<>(newsItems), new ArrayList<>(newsCardLayoutData));
                }
            } else if ("refresh".equals(updateType)) {
                List<NewsItem> newsItems = repository.getNewsData().getValue();
                List<NewsCardLayout> newsCardLayoutData = repository.getCardLayoutData().getValue();
                if (newsItems != null && newsCardLayoutData != null) {
                    newsAdapter.refreshData(new ArrayList<>(newsItems), new ArrayList<>(newsCardLayoutData));
                }
            }
        });

        // 监听加载状态
        repository.getIsLoading().observeForever(isLoading -> {
            if (isLoading != null) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });

        // 监听错误
        repository.getError().observeForever(errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}