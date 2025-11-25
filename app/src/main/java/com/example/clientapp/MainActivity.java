package com.example.clientapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.clientapp.NewsAdapter;
import com.example.clientapp.NewsItem;
import com.example.clientapp.ServerDataCallback;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView RV;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private long lastExposureCheckTime = 0;
    private static final long EXPOSURE_CHECK_INTERVAL = 500;

    private Map<Integer, Boolean> cardExposedStatus = new HashMap<>();
    private Map<Integer, Boolean> cardHalfExposedStatus = new HashMap<>();
    private Map<Integer, Boolean> cardFullyExposedStatus = new HashMap<>();

    private List<String> exposureEvents = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private MockServer mockServer = new MockServer();

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

        RV = findViewById(R.id.recyclerview);
        RV.setLayoutManager(new LinearLayoutManager(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                assert tab.getText() != null;
                if ("测试".equals(tab.getText().toString())) {
                    Log.d("EXPOSURE_DEBUG", "曝光事件: " + getExposureEvents());
                    Intent intent = new Intent(MainActivity.this, TestToolActivity.class);
                    intent.putStringArrayListExtra("EXPOSURE_EVENTS", new ArrayList<>(getExposureEvents()));
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        RV.setAdapter(newsAdapter);
        loadMoreNews();

        RV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = newsAdapter.getItemCount();

                if (System.currentTimeMillis() - lastExposureCheckTime > EXPOSURE_CHECK_INTERVAL) {
                    checkExposure(firstVisibleItem, lastVisibleItem, totalItemCount);
                    lastExposureCheckTime = System.currentTimeMillis();
                }

                if (!newsAdapter.isLoading && lastVisibleItem >= totalItemCount - 2) {
                    loadMoreNews();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = newsAdapter.getItemCount();
                    checkExposure(firstVisibleItem, lastVisibleItem, totalItemCount);
                }
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            // 使用MockServer获取新数据（修复：使用正确类型）
            mockServer.fetchDataFromServer(new ServerDataCallback() {
                @Override
                public void onSuccess(List<NewsItem> data) {
                    newsAdapter.clearData();
                    newsAdapter.addMoreItems(data);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        newsAdapter.setOnCardDeleteListener(position -> {
            showDeleteConfirmationDialog(position);
        });
    }

    private void loadMoreNews() {
        newsAdapter.isLoading = true;
        mockServer.fetchDataFromServer(new ServerDataCallback() {
            @Override
            public void onSuccess(List<NewsItem> data) {
                newsAdapter.addMoreItems(data);
                newsAdapter.isLoading = false;
            }

            @Override
            public void onFailure(String errorMessage) {
                newsAdapter.isLoading = false;
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkExposure(int firstVisibleItem, int lastVisibleItem, int totalItemCount) {
        // 曝光检测逻辑
        LinearLayoutManager layoutManager = (LinearLayoutManager) RV.getLayoutManager();
        if (layoutManager == null) return;
        // int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        // int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        // int totalItemCount = newsAdapter.getItemCount();

        int[] recyclerViewLocation = new int[2];
        RV.getLocationOnScreen(recyclerViewLocation);
        int recyclerViewTop = recyclerViewLocation[1];
        int recyclerViewBottom = recyclerViewLocation[1] + RV.getHeight();

        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            if (i >= totalItemCount) break;
            int childIndex = i - firstVisibleItem;
            if (childIndex < 0 || childIndex >= RV.getChildCount()) continue;
            View view = RV.getChildAt(childIndex);
            if (view == null) continue;

            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int viewTop = location[1];
            int viewBottom = viewTop + view.getHeight();

            int visibleTop = Math.max(viewTop, recyclerViewTop);
            int visibleBottom = Math.min(viewBottom, recyclerViewBottom);
            int visibleHeight = Math.max(0, visibleBottom - visibleTop);
            float visibilityRatio = (float) visibleHeight / view.getHeight();

            if (visibilityRatio > 0.0f && !cardExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardExposedStatus.put(i, true);
            }

            if (visibilityRatio >= 0.5f && !cardHalfExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片露出超过50%%: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardHalfExposedStatus.put(i, true);
            }

            if (visibilityRatio >= 0.9f && !cardFullyExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片完整露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardFullyExposedStatus.put(i, true);
            }

            if (visibilityRatio <= 0.2f && cardFullyExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片消失: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardExposedStatus.put(i, false);
                cardHalfExposedStatus.put(i, false);
                cardFullyExposedStatus.put(i, false);
            }
        }
    }

    public List<String> getExposureEvents() {
        return new ArrayList<>(exposureEvents);
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除新闻")
                .setMessage("确定要删除这条新闻吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    newsAdapter.removeItem(position);
                    Toast.makeText(this, "新闻已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}