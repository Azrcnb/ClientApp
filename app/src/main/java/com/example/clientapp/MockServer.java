package com.example.clientapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;
import com.example.clientapp.NewsItem;

// MockServer类（独立于接口）
public class MockServer {
    /**
     * 模拟服务器数据请求，包含请求成功和失败两种情况
     * @param callback 请求完成后的回调
     */
    public void fetchDataFromServer(ServerDataCallback callback) {
        // 模拟请求延迟时间（500ms-2000ms随机波动）
        final int delay = (int) (Math.random() * 1500 + 500);

        // 模拟请求成功概率（70%成功，30%失败）
        boolean isSuccess = Math.random() < 0.7;

        new Handler().postDelayed(() -> {
            if (isSuccess) {
                // 请求成功，返回模拟数据
                List<NewsItem> mockData = generateMockData();
                callback.onSuccess(mockData);
            } else {
                // 请求失败，返回错误信息
                String errorMessage = "网络请求失败，请检查网络连接";
                callback.onFailure(errorMessage);
            }
        }, delay);
    }

    /**
     * 生成模拟数据
     * @return 模拟的新闻数据列表
     */
    private List<NewsItem> generateMockData() {
        List<NewsItem> mockData = new ArrayList<>();

        // 添加15条模拟新闻数据
        mockData.add(new NewsItem("江苏下雪了", "江苏多地下雪！气温暴跌7℃！接下来天气大反转", R.drawable.new1));
        mockData.add(new NewsItem("科技新动态", "苹果发布全新智能手表，支持健康监测", R.drawable.new2));
        mockData.add(new NewsItem("娱乐头条", "新电影《星际穿越2》票房破纪录", R.drawable.new3));
        mockData.add(new NewsItem("健康快讯", "专家提醒：冬季流感高发期，这些防护措施必看", R.drawable.new4));
        mockData.add(new NewsItem("财经聚焦", "央行宣布降准0.25%，房贷利率将下调", R.drawable.new5));
        mockData.add(new NewsItem("体育新闻", "中国女排3:0横扫巴西，晋级世锦赛四强", R.drawable.new6));
        mockData.add(new NewsItem("教育资讯", "新高考改革方案出台，选科模式大调整", R.drawable.new7));
        mockData.add(new NewsItem("美食推荐", "冬日暖心食谱：当归羊肉汤做法大公开", R.drawable.new8));
        mockData.add(new NewsItem("旅游攻略", "冬季赏雪胜地推荐：哈尔滨冰雪大世界开放", R.drawable.new9));
        mockData.add(new NewsItem("环保动态", "全国首个碳中和示范区在杭州启动", R.drawable.new10));
        mockData.add(new NewsItem("AI要闻", "科技巨头发布AI新模型", R.drawable.new11));
        mockData.add(new NewsItem("天气预报", "北京今晨-5℃，北方寒潮预警，公交地铁提前30分钟发车", R.drawable.new12));
        mockData.add(new NewsItem("体育快讯", "2025世乒赛男团决赛，王楚钦逆转夺冠，中国队第20次包揽", R.drawable.new13));
        mockData.add(new NewsItem("财经聚焦", "A股光伏板块暴涨8%，宁德时代签100亿海外订单", R.drawable.new14));
        mockData.add(new NewsItem("健康提醒", "流感季首波高峰，卫健委推'三件套'：疫苗+通风+勤洗手", R.drawable.new15));

        // 随机选择5条新闻
        Collections.shuffle(mockData);
        return new ArrayList<>(mockData.subList(0, 5));
    }
}