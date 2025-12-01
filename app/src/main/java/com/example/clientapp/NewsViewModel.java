package com.example.clientapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.clientapp.Model.MockServer;
import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.Model.ServerDataCallback;
import java.util.ArrayList;
import java.util.List;

public class NewsViewModel extends ViewModel {
    // 1. 新增 CardTypeData 的 LiveData（和 newsData 平等管理！）
    private MutableLiveData<List<NewsCardLayout>> cardLayoutData = new MutableLiveData<>();
    private MutableLiveData<List<NewsItem>> newsData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<String> updateType = new MutableLiveData<>();

    // 2. 新增成员变量，用于保存所有新闻数据
    private List<NewsItem> currentNewsData;
    private List<NewsCardLayout> currentCardLayoutData;

    public NewsViewModel() {
        // 初始化数据（必须包含 CardTypeData）
        newsData.setValue(new ArrayList<>());
        cardLayoutData.setValue(new ArrayList<>());
        // 初始化 CardTypeData
        isLoading.setValue(false);
        // 初始化成员变量
        currentNewsData = new ArrayList<>();
        currentCardLayoutData = new ArrayList<>();
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

    public List<NewsItem> getCurrentNewsData() {
        return currentNewsData;
    }

    public List<NewsCardLayout> getCurrentCardLayoutData() {
        return currentCardLayoutData;
    }

    public LiveData<String> getError() {
        return error;
    }

    // 新增 getUpdateType 方法
    public LiveData<String> getUpdateType() {
        return updateType;
    }

    public void loadMoreNews() {
        isLoading.setValue(true);
        MockServer mockServer = new MockServer();
        mockServer.fetchDataFromServer(new ServerDataCallback() {
            @Override
            public void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockNewsCardLayoutData, String successMessage) {
                // 计算当前数据长度作为偏移量
                int offset = currentNewsData.size();
                // 创建副本以避免并发修改

                // ✅ 关键修复：更新 CardTypeData 索引（追加到现有数据）
                List<NewsCardLayout> updatedCardLayoutData = new ArrayList<>();
                for (NewsCardLayout layout : mockNewsCardLayoutData) {
                    if (layout.getType() == NewsAdapter.CARD_TYPE_SINGLE) {
                        // 单列：偏移左索引
                        updatedCardLayoutData.add(new NewsCardLayout(
                                layout.getType(), layout.getLeftNewIndex() + offset));
                    } else {
                        // 双列：偏移左右索引
                        updatedCardLayoutData.add(new NewsCardLayout(
                                layout.getType(), layout.getLeftNewIndex() + offset, layout.getRightNewIndex() + offset));
                    }
                }

                // 更新成员变量
                currentNewsData.addAll(mockNewsData);
                currentCardLayoutData.addAll(updatedCardLayoutData);

                // 更新 LiveData
                newsData.setValue(new ArrayList<>(mockNewsData));
                cardLayoutData.setValue(new ArrayList<>(updatedCardLayoutData));

                // 设置更新类型为"loadMore"
                updateType.setValue("loadMore");

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
        // 重置成员变量
        currentNewsData = new ArrayList<>();
        currentCardLayoutData = new ArrayList<>();

        MockServer mockServer = new MockServer();
        mockServer.fetchDataFromServer(new ServerDataCallback() {
            @Override
            public void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockCardTypeData, String successMessage) {
                // 更新成员变量
                currentNewsData = new ArrayList<>(mockNewsData);
                currentCardLayoutData = new ArrayList<>(mockCardTypeData);

                // 更新 LiveData
                newsData.setValue(new ArrayList<>(currentNewsData));
                cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

                // 设置更新类型为"refresh"
                updateType.setValue("refresh");

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
                currentCardLayoutData.get(position).setRightNewIndex(-1);
            } else {
                deleteNewIndex = currentCardLayoutData.get(position).getRightNewIndex();
                currentNewsData.remove(deleteNewIndex);
                currentCardLayoutData.get(position).setType(NewsAdapter.CARD_TYPE_SINGLE);
                currentCardLayoutData.get(position).setRightNewIndex(-1);
            }
        }

        // 更新所有后续卡片的索引
        for (int i = 0; i < currentCardLayoutData.size(); i++) {
            NewsCardLayout layout = currentCardLayoutData.get(i);
            if (layout.getType() == NewsAdapter.CARD_TYPE_SINGLE) {
                if (layout.getLeftNewIndex() > deleteNewIndex) {
                    layout.setLeftNewIndex(layout.getLeftNewIndex() - 1);
                }
            } else {
                // 更新 leftNewIndex
                if (layout.getLeftNewIndex() > deleteNewIndex) {
                    layout.setLeftNewIndex(layout.getLeftNewIndex() - 1);
                }
                // 更新 rightNewIndex
                if (layout.getRightNewIndex() > deleteNewIndex) {
                    layout.setRightNewIndex(layout.getRightNewIndex() - 1);
                }
            }


        }
    }
}