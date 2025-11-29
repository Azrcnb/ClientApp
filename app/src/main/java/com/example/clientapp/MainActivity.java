package com.example.clientapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.clientapp.ListenerManager.ExposureTracker;
import com.example.clientapp.ListenerManager.UserInteractionListener;
import com.example.clientapp.ListenerManager.ViewModelListener;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView RV;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化组件
        // 初始化recyclerview组件
        RV = findViewById(R.id.recyclerview);
        RV.setLayoutManager(new LinearLayoutManager(this));
        // 初始化 Adapter
        newsAdapter = new NewsAdapter(this, new ArrayList<>(), new ArrayList<>());
        RV.setAdapter(newsAdapter);
        // 初始化 ViewModel
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // 初始化监听器
        // 初始化用户交互监听器
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        new UserInteractionListener(this, RV, swipeRefreshLayout, newsAdapter, newsViewModel, tabLayout);
        // 初始化 ViewModel监听器
        new ViewModelListener(newsViewModel, newsAdapter, swipeRefreshLayout, this);

        // 初始化数据
        newsViewModel.loadMoreNews();
    }
}