package com.example.clientapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class NewsViewModel extends ViewModel {
    // 1. 新增 CardTypeData 的 LiveData（和 newsData 平等管理！）
    private MutableLiveData<List<NewsCardLayout>> cardLayoutData = new MutableLiveData<>();
    private MutableLiveData<List<NewsItem>> newsData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public NewsViewModel() {
        // 初始化数据（必须包含 CardTypeData）
        newsData.setValue(new ArrayList<>());
        cardLayoutData.setValue(new ArrayList<>());
        // 重点：初始化 CardTypeData
        isLoading.setValue(false);
    }

    // 2. 新增 CardTypeData 的访问接口（其他地方可直接调用！）
    public LiveData<List<NewsCardLayout>> getCardLayoutData() {
        return cardLayoutData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<NewsItem>> getNewsData() {
        return newsData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMoreNews() {
        isLoading.setValue(true);
        MockServer mockServer = new MockServer();
        mockServer.fetchDataFromServer(new ServerDataCallback() {
            @Override
            public void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockNewsCardLayoutData, String successMessage) {
                // ✅ 关键修复：计算当前数据长度作为偏移量
                int offset = newsData.getValue().size();

                // 创建副本以避免并发修改
                List<NewsItem> currentNews = new ArrayList<>(newsData.getValue());
                List<NewsCardLayout> currentCardLayoutData = new ArrayList<>(cardLayoutData.getValue());

                // ✅ 关键修复：更新 CardTypeData 索引（追加到现有数据）
                List<NewsCardLayout> updatedCardLayoutData = new ArrayList<>();
                for (NewsCardLayout layout : mockNewsCardLayoutData) {
                    if (layout.getType() == NewsAdapter.CARD_TYPE_SINGLE) {
                        // 单列：偏移左索引
                        updatedCardLayoutData.add(new NewsCardLayout(
                                layout.getType(),
                                layout.getLeftNewIndex() + offset
                        ));
                    } else {
                        // 双列：偏移左右索引
                        updatedCardLayoutData.add(new NewsCardLayout(
                                layout.getType(),
                                layout.getLeftNewIndex() + offset,
                                layout.getRightNewIndex() + offset
                        ));
                    }
                }

                // 追加 CardType 数据
                currentCardLayoutData.addAll(updatedCardLayoutData);
                cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

                // 追加新闻数据
                currentNews.addAll(mockNewsData);
                newsData.setValue(new ArrayList<>(currentNews));

                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    public void refreshNews() {
        isLoading.setValue(true);
        // 5. 刷新时重置 CardTypeData（因为是全新数据）
        cardLayoutData.setValue(new ArrayList<>());
        newsData.setValue(new ArrayList<>());

        MockServer mockServer = new MockServer();
        mockServer.fetchDataFromServer(new ServerDataCallback() {
            @Override
            public void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockCardTypeData, String successMessage) {
                // 6. 刷新时设置全新的 CardTypeData
                cardLayoutData.setValue(new ArrayList<>(mockCardTypeData));
                newsData.setValue(new ArrayList<>(mockNewsData));
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }

    // 在 NewsViewModel.java 中添加以下方法
    public void removeNews(int position, boolean isLeftColumn) {
        List<NewsItem> currentNewsData = newsData.getValue();
        List<NewsCardLayout> currentCardLayoutData = cardLayoutData.getValue();
        if (currentNewsData == null || currentCardLayoutData == null || position < 0 || position >= currentNewsData.size()) {
            return;
        }

        int cardType = currentCardLayoutData.get(position).getType();
        int deleteNewIndex = -1;

        if (cardType == NewsAdapter.CARD_TYPE_SINGLE) {
            // 单列卡片：直接删除整个卡片
            deleteNewIndex = currentCardLayoutData.get(position).getLeftNewIndex();
            currentNewsData.remove(deleteNewIndex);
            currentCardLayoutData.remove(position);
        } else if (cardType == NewsAdapter.CARD_TYPE_DOUBLE) {
            if (isLeftColumn) {
                deleteNewIndex = currentCardLayoutData.get(position).getLeftNewIndex();
                currentNewsData.remove(deleteNewIndex);
                currentCardLayoutData.get(position).setType(NewsAdapter.CARD_TYPE_SINGLE);
            } else {
                deleteNewIndex = currentCardLayoutData.get(position).getRightNewIndex();
                currentNewsData.remove(deleteNewIndex);
                currentCardLayoutData.get(position).setType(NewsAdapter.CARD_TYPE_SINGLE);
            }
        }

        // ✅ 关键修复：更新所有后续卡片的索引
        for (int i = 0; i < currentCardLayoutData.size(); i++) {
            NewsCardLayout layout = currentCardLayoutData.get(i);

            // 更新 leftNewIndex
            if (layout.getLeftNewIndex() > deleteNewIndex) {
                layout.setLeftNewIndex(layout.getLeftNewIndex() - 1);
            }

            // 更新 rightNewIndex
            if (layout.getRightNewIndex() > deleteNewIndex) {
                layout.setRightNewIndex(layout.getRightNewIndex() - 1);
            }
        }

        // 更新 LiveData
        newsData.setValue(currentNewsData);
        cardLayoutData.setValue(currentCardLayoutData);
    }
}