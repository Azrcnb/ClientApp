package com.example.clientapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.graphics.Rect;
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
import com.example.clientapp.adapter.NewsAdapter;
import com.example.clientapp.model.NewsItem;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView RV; // 声明RecyclerView实例
    private NewsAdapter newsAdapter; // 声明NewsAdapter实例
    private SwipeRefreshLayout swipeRefreshLayout;
    private long lastExposureCheckTime = 0;
    private static final long EXPOSURE_CHECK_INTERVAL = 500; // 500ms

    // 用于跟踪卡片曝光状态
    private Map<Integer, Boolean> cardExposedStatus = new HashMap<>();
    private Map<Integer, Boolean> cardHalfExposedStatus = new HashMap<>();
    private Map<Integer, Boolean> cardFullyExposedStatus = new HashMap<>();

    // 用于存储曝光事件
    private List<String> exposureEvents = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 启用EdgeToEdge特性（处理状态栏和导航栏）
        setContentView(R.layout.activity_main); // 设置主界面布局

        // ========== 1. 窗口Insets处理（适配状态栏和导航栏） ==========
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ========== 2. 初始化RecyclerView（关键步骤） ==========
        RV = findViewById(R.id.recyclerview); // 从布局中获取RecyclerView实例
        RV.setLayoutManager(new LinearLayoutManager(this)); // 设置线性布局管理器（垂直方向）

        // 2. 初始化 TabLayout（关键位置！）
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                assert tab.getText() != null;
                if ("测试".equals(tab.getText().toString())) {
                    // 打印曝光事件到Logcat
                    Log.d("EXPOSURE_DEBUG", "曝光事件: " + getExposureEvents());
                    // 保持原有跳转逻辑
                    Intent intent = new Intent(MainActivity.this, TestToolActivity.class);
                    intent.putStringArrayListExtra("EXPOSURE_EVENTS", new ArrayList<>(getExposureEvents()));
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中时的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 重新选中时的逻辑
            }
        });

        // ========== 3. 准备初始新闻数据 ==========
        List<NewsItem> initialNewsList = new ArrayList<>();
        // 添加初始新闻项
        initialNewsList.add(new NewsItem("江苏下雪了", "江苏多地下雪！气温暴跌7℃！接下来天气大反转", R.drawable.new1));

        // ========== 4. 创建并设置NewsAdapter ==========
        newsAdapter = new NewsAdapter(this, initialNewsList); // 创建适配器
        RV.setAdapter(newsAdapter); // 将适配器绑定到RecyclerView

        // ========== 5. 实现无限滚动功能（核心修改） ==========
        RV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 获取当前可见的最后一个项的位置
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                // ========== 条件判断（触发加载更多） ==========
                // 1. 检查是否正在加载中（避免重复请求）
                // 2. 检查是否已到达最后一页（避免继续请求）
                // 3. 检查是否滚动到底部（最后可见项接近列表末尾）
                if (!newsAdapter.isLoading && lastVisibleItem >= totalItemCount - 5) {

                    // 模拟加载更多新闻（实际应用中应替换为网络请求）
                    loadMoreNews();
                }

                // 检查是否需要进行曝光检测
                if (System.currentTimeMillis() - lastExposureCheckTime > EXPOSURE_CHECK_INTERVAL) {
                    checkExposure();
                    lastExposureCheckTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkExposure();
                }
            }
        });

        // ===== 新增：下拉刷新功能（关键整合） =====
        swipeRefreshLayout = findViewById(R.id.swipe_refresh); // 从布局中获取SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 1. 显示刷新动画
            swipeRefreshLayout.setRefreshing(true);
            // 2. 模拟网络请求（1.5秒后完成）
            new Handler().postDelayed(() -> {
                List<NewsItem> newData = fetchNewData();
                newsAdapter.clearData(); // 清空数据
                newsAdapter.addAll(newData); // 添加新数据
                // 5. 停止刷新动画
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
            }, 1500);
        });

        // ===== 设置删除监听器 =====
        newsAdapter.setOnCardDeleteListener(position -> {
            showDeleteConfirmationDialog(position);
        });
    }

    private void checkExposure() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) RV.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = newsAdapter.getItemCount();

        // 获取屏幕可见区域
        Rect displayFrame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayFrame);
        int screenHeight = displayFrame.height();

        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            if (i >= totalItemCount) break;

            View view = RV.getLayoutManager().findViewByPosition(i);
            if (view == null) continue;

            int[] location = new int[2];
            view.getLocationOnScreen(location);

            int viewTop = location[1];
            int viewBottom = viewTop + view.getHeight();
            int visibleHeight = Math.max(0,
                    Math.min(viewBottom, displayFrame.bottom) -
                            Math.max(viewTop, displayFrame.top));

            float visibilityRatio = (float) visibleHeight / view.getHeight();

            // 卡片露出
            if (visibleHeight > 0 && !cardExposedStatus.getOrDefault(i, false)) {

                if (i>=1) {
                    String event0 = String.format("卡片消失: %d - %s", i-1, timeFormat.format(new java.util.Date()));
                    exposureEvents.add(event0);
                    cardExposedStatus.put(i-1, false);
                }

                String event = String.format("卡片露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardExposedStatus.put(i, true);
            }

            // 卡片露出超过50%
            if (visibilityRatio >= 0.5f && !cardHalfExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片露出超过50%%: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardHalfExposedStatus.put(i, true);
            }

            // 卡片完整露出
            if (visibilityRatio >= 0.9f && !cardFullyExposedStatus.getOrDefault(i, false)) {
                String event = String.format("卡片完整露出: %d - %s", i, timeFormat.format(new java.util.Date()));
                exposureEvents.add(event);
                cardFullyExposedStatus.put(i, true);
            }

            // 卡片消失
            if (visibleHeight <= 0 && cardExposedStatus.getOrDefault(i, false)) {
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

    private List<NewsItem> fetchNewData() {
        List<NewsItem> newData = new ArrayList<>();
        newData.add(new NewsItem("最新新闻", "科技巨头发布AI新模型", R.drawable.new11));
        newData.add(new NewsItem("天气预报", "北京今晨-5℃，北方寒潮预警，公交地铁提前30分钟发车", R.drawable.new12));
        newData.add(new NewsItem("体育快讯", "2025世乒赛男团决赛，王楚钦逆转夺冠，中国队第20次包揽", R.drawable.new13));
        newData.add(new NewsItem("财经聚焦", "A股光伏板块暴涨8%，宁德时代签100亿海外订单", R.drawable.new14));
        newData.add(new NewsItem("健康提醒", "流感季首波高峰，卫健委推'三件套'：疫苗+通风+勤洗手", R.drawable.new15));
        return newData;
    }

    // 模拟加载更多新闻数据
    private void loadMoreNews() {
        // 创建模拟的新闻数据
        List<NewsItem> newNewsItems = new ArrayList<>();
        newNewsItems.add(new NewsItem("江苏下雪了", "江苏多地下雪！气温暴跌7℃！接下来天气大反转", R.drawable.new1));
        newNewsItems.add(new NewsItem("科技新动态", "苹果发布全新智能手表，支持健康监测", R.drawable.new2));
        newNewsItems.add(new NewsItem("娱乐头条", "新电影《星际穿越2》票房破纪录", R.drawable.new3));
        newNewsItems.add(new NewsItem("健康快讯", "专家提醒：冬季流感高发期，这些防护措施必看", R.drawable.new4));
        newNewsItems.add(new NewsItem("财经聚焦", "央行宣布降准0.25%，房贷利率将下调", R.drawable.new5));
        newNewsItems.add(new NewsItem("体育新闻", "中国女排3:0横扫巴西，晋级世锦赛四强", R.drawable.new6));
        newNewsItems.add(new NewsItem("教育资讯", "新高考改革方案出台，选科模式大调整", R.drawable.new7));
        newNewsItems.add(new NewsItem("美食推荐", "冬日暖心食谱：当归羊肉汤做法大公开", R.drawable.new8));
        newNewsItems.add(new NewsItem("旅游攻略", "冬季赏雪胜地推荐：哈尔滨冰雪大世界开放", R.drawable.new9));
        newNewsItems.add(new NewsItem("环保动态", "全国首个碳中和示范区在杭州启动", R.drawable.new10));
        newNewsItems.add(new NewsItem("AI要闻", "科技巨头发布AI新模型", R.drawable.new11));
        newNewsItems.add(new NewsItem("天气预报", "北京今晨-5℃，北方寒潮预警，公交地铁提前30分钟发车", R.drawable.new12));
        newNewsItems.add(new NewsItem("体育快讯", "2025世乒赛男团决赛，王楚钦逆转夺冠，中国队第20次包揽", R.drawable.new13));
        newNewsItems.add(new NewsItem("财经聚焦", "A股光伏板块暴涨8%，宁德时代签100亿海外订单", R.drawable.new14));
        newNewsItems.add(new NewsItem("健康提醒", "流感季首波高峰，卫健委推'三件套'：疫苗+通风+勤洗手", R.drawable.new15));

        // 触发加载更多
        newsAdapter.addMoreItems(newNewsItems);
    }

    // 显示删除确认对话框
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除新闻")
                .setMessage("确定要删除这条新闻吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 执行删除操作
                    newsAdapter.removeItem(position);
                    Toast.makeText(this, "新闻已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}