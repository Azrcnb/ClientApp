package com.example.clientapp.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;

import com.example.clientapp.R;

// MockServer类（独立于接口）
public class MockServer {
    public static final int CARD_TYPE_SINGLE = 0; // 单列布局
    public static final int CARD_TYPE_DOUBLE = 1; // 双列布局
    public static final int CARD_TYPE_VIDEO = 2;
    public static final int News_List_Size_Once = 10;

    /**
     * 模拟服务器数据请求，包含请求成功和失败两种情况
     * @param callback 请求完成后的回调
     */
    public void fetchDataFromServer(ServerDataCallback callback) {
        // 模拟请求延迟时间（500ms-2000ms随机波动）
        final int delay = (int) (Math.random() * 1500 + 500);
        // 模拟请求成功概率（70%成功，30%失败）
        boolean isSuccess = Math.random() < 0.9;
        new Handler().postDelayed(() -> {
            if (isSuccess) {
                // 请求成功，返回模拟数据
                List<NewsItem> mockNewsData = generateMockData();
                List<NewsCardLayout> mockNewsCardLayoutData = generateNewsCardLayoutList(); // 生成CardLayout列表
                String successMessage = "刷新成功";
                callback.onSuccess(mockNewsData, mockNewsCardLayoutData, successMessage);
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
        mockData.add(new NewsItem("传统文化", "探秘中国传统知识：文化秘诀与智慧结晶", R.drawable.video_view, R.raw.test_video));

        // 随机选择5条新闻
        Collections.shuffle(mockData);
        return new ArrayList<>(mockData.subList(0, News_List_Size_Once));
    }

    /**
     * 生成卡片布局信息列表（替代原有的generatecardTypeList）
     * @return CardLayout对象列表，包含卡片类型和绑定的新闻索引
     */
    private List<NewsCardLayout> generateNewsCardLayoutList() {
        List<NewsCardLayout> cardLayoutList = new ArrayList<>();
        int currentNewsIndex = 0;

        // 生成卡片布局，确保总新闻数等于5
        while (currentNewsIndex < News_List_Size_Once) {
            int type = Math.random() < 0.5 ? CARD_TYPE_SINGLE : CARD_TYPE_DOUBLE;

            if (type == CARD_TYPE_SINGLE) {
                cardLayoutList.add(new NewsCardLayout(CARD_TYPE_SINGLE, currentNewsIndex));
                currentNewsIndex++;
            } else { // CARD_TYPE_DOUBLE
                // 确保不会越界
                if (currentNewsIndex + 1 < News_List_Size_Once) {
                    cardLayoutList.add(new NewsCardLayout(CARD_TYPE_DOUBLE, currentNewsIndex, currentNewsIndex + 1));
                    currentNewsIndex += 2;
                } else {
                    // 如果只剩1条新闻，强制改为单列
                    cardLayoutList.add(new NewsCardLayout(CARD_TYPE_SINGLE, currentNewsIndex));
                    currentNewsIndex++;
                }
            }
        }

        return cardLayoutList;
    }
}