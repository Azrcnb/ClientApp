package com.example.clientapp.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.clientapp.Adapter.NewsAdapter;
import com.example.clientapp.ViewModel.UserInteractionViewModel;
import com.example.clientapp.Repository.RepositoryListener;
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
        RecyclerView rv = findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // 初始化刷新组件
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        // 初始化 repository
        // 初始化 repository
        Repository repository = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass == Repository.class) { //
                    // 传递 Application Context，避免内存泄漏
                    return (T) new Repository(MainActivity.this.getApplicationContext());
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(Repository.class);
        // 初始化 Adapter
        NewsAdapter newsAdapter = new NewsAdapter(new ArrayList<>(), new ArrayList<>());
        rv.setAdapter(newsAdapter);

        // 初始化监听器
        // 初始化本地仓库监听器
        new RepositoryListener(repository, newsAdapter, swipeRefreshLayout, this);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        // 初始化用户交互监听器
        new UserInteractionViewModel(this, rv, swipeRefreshLayout, newsAdapter, repository, tabLayout);


    }
}