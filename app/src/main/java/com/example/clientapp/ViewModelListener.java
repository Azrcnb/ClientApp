package com.example.clientapp;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.widget.Toast;
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
        newsViewModel.getNewsData().observeForever(newsItems -> {
            if (newsItems != null) {
                List<NewsCardLayout> newsCardLayoutData = newsViewModel.getCardLayoutData().getValue();
                if (newsCardLayoutData != null) {
                    ((NewsAdapter) newsAdapter).updateData(new ArrayList<>(newsItems), new ArrayList<>(newsCardLayoutData));
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