package com.example.clientapp.Activity;

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

import com.example.clientapp.Adapter.NewsAdapter;
import com.example.clientapp.ViewModel.UserInteractionViewModel;
import com.example.clientapp.ViewModel.RepositoryViewModel;
import com.example.clientapp.R;
import com.example.clientapp.Repository.Repository;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        RecyclerView RV = findViewById(R.id.recyclerview);
        RV.setLayoutManager(new LinearLayoutManager(this));
        // 初始化刷新组件
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        // 初始化 repository
        Repository repository = new ViewModelProvider(this).get(Repository.class);
        repository.refreshNews();
        // 初始化 Adapter
        NewsAdapter newsAdapter = new NewsAdapter(new ArrayList<>(), new ArrayList<>());
        RV.setAdapter(newsAdapter);

        // 初始化监听器
        // 初始化本地仓库监听器
        new RepositoryViewModel(repository, newsAdapter, swipeRefreshLayout, this);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        // 初始化用户交互监听器
        new UserInteractionViewModel(this, RV, swipeRefreshLayout, newsAdapter, repository, tabLayout);


    }
}