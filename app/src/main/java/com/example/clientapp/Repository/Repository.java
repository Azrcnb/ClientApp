package com.example.clientapp.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;

import com.example.clientapp.Adapter.NewsAdapter;
import com.example.clientapp.Server.MockServer;
import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;
import com.example.clientapp.Server.ServerDataCallback;
import java.util.ArrayList;
import java.util.List;

public class Repository extends ViewModel {
    // 声明CardTypeData 的 LiveData
    private final MutableLiveData<List<NewsCardLayout>> cardLayoutData = new MutableLiveData<>();
    private final MutableLiveData<List<NewsItem>> newsData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> updateType = new MutableLiveData<>();

    // 声明currentNewsData和currentCardLayoutData，用于保存所有新闻数据
    private List<NewsItem> currentNewsData;
    private List<NewsCardLayout> currentCardLayoutData;

    // 缓存管理器
    private final CacheManager cacheManager;

    // 修改构造方法，接收 Context
    public Repository(Context context) {
        Context appContext = context.getApplicationContext();
        this.cacheManager = new CacheManager(appContext);

        // 初始化数据（必须包含 CardTypeData）
        newsData.setValue(new ArrayList<>());
        cardLayoutData.setValue(new ArrayList<>());
        // 初始化 CardTypeData
        isLoading.setValue(false);
        // 初始化成员变量
        currentNewsData = new ArrayList<>();
        currentCardLayoutData = new ArrayList<>();

        // 首次创建 Repository 时尝试从缓存加载
        loadFromCacheOnStart();
    }

    // CardTypeData 的访问接口
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

    // getUpdateType 方法
    public LiveData<String> getUpdateType() {
        return updateType;
    }

    // 启动时从缓存加载
    private void loadFromCacheOnStart() {
        CacheManager.CacheResult cacheResult = cacheManager.loadCache();
        if (cacheResult != null) {
            currentNewsData = new ArrayList<>(cacheResult.newsData);
            currentCardLayoutData = new ArrayList<>(cacheResult.cardData);

            // 更新LiveData
            newsData.setValue(new ArrayList<>(currentNewsData));
            cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

            // 设置更新类型
            updateType.setValue("refresh");
            error.setValue("正在使用缓存数据");
        }
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

                // 更新 CardTypeData 索引（追加到现有数据）
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

                // 保存到缓存
                cacheManager.saveCache(currentNewsData, currentCardLayoutData);

                // 更新 LiveData
                newsData.setValue(new ArrayList<>(mockNewsData));
                cardLayoutData.setValue(new ArrayList<>(updatedCardLayoutData));

                // 设置更新类型为"loadMore"
                updateType.setValue("loadMore");
                error.setValue("加载成功");
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 失败时检查缓存
                CacheManager.CacheResult cacheResult = cacheManager.loadCache();
                if (cacheResult != null) {
                    // 使用缓存数据
                    currentNewsData = new ArrayList<>(cacheResult.newsData);
                    currentCardLayoutData = new ArrayList<>(cacheResult.cardData);

                    newsData.setValue(new ArrayList<>(currentNewsData));
                    cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

                    updateType.setValue("refresh");
                    error.setValue("网络异常，已显示缓存内容");
                } else {
                    // 没有缓存才显示真正的错误
                    error.setValue(errorMessage);
                }
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

                // 保存到缓存
                cacheManager.saveCache(currentNewsData, currentCardLayoutData);

                // 更新 LiveData
                newsData.setValue(new ArrayList<>(currentNewsData));
                cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

                // 设置更新类型为"refresh"
                updateType.setValue("refresh");
                error.setValue("刷新成功"); // 清除错误信息
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 刷新失败时尝试使用缓存
                CacheManager.CacheResult cacheResult = cacheManager.loadCache();
                if (cacheResult != null) {
                    // 使用缓存数据
                    currentNewsData = new ArrayList<>(cacheResult.newsData);
                    currentCardLayoutData = new ArrayList<>(cacheResult.cardData);

                    newsData.setValue(new ArrayList<>(currentNewsData));
                    cardLayoutData.setValue(new ArrayList<>(currentCardLayoutData));

                    updateType.setValue("refresh");
                    error.setValue("刷新失败，已显示缓存内容");
                } else {
                    // 没有缓存才显示真正的错误
                    error.setValue(errorMessage);
                }
                isLoading.setValue(false);
            }
        });
    }

    // 在 NewsViewModel.java 中添加以下方法
    public void removeNews(int position, boolean isLeftColumn) {
        // 创建深拷贝副本（关键：确保副本独立）
        List<NewsItem> newNewsData = new ArrayList<>(currentNewsData);
        List<NewsCardLayout> newCardLayoutData = new ArrayList<>();

        // 深拷贝每个NewsCardLayout（避免属性污染）
        for (NewsCardLayout layout : currentCardLayoutData) {
            NewsCardLayout copy = new NewsCardLayout(
                    layout.getType(),
                    layout.getLeftNewIndex(),
                    layout.getRightNewIndex()
            );
            newCardLayoutData.add(copy);
        }

        // 检查边界条件（在副本上操作）
        if (position < 0 || position >= newCardLayoutData.size()) {
            return;
        }

        // 执行删除操作（在副本上）
        int cardType = newCardLayoutData.get(position).getType();
        int deleteNewIndex = -1;

        if (cardType == NewsAdapter.CARD_TYPE_SINGLE) {
            deleteNewIndex = newCardLayoutData.get(position).getLeftNewIndex();
            newNewsData.remove(deleteNewIndex);
            newCardLayoutData.remove(position);
        } else if (cardType == NewsAdapter.CARD_TYPE_DOUBLE) {
            if (isLeftColumn) {
                deleteNewIndex = newCardLayoutData.get(position).getLeftNewIndex();
            } else {
                deleteNewIndex = newCardLayoutData.get(position).getRightNewIndex();
            }
            newNewsData.remove(deleteNewIndex);

            // 更新当前卡片类型为单列
            newCardLayoutData.get(position).setType(NewsAdapter.CARD_TYPE_SINGLE);
            newCardLayoutData.get(position).setRightNewIndex(-1);
        }

        // 更新后续卡片索引（在副本上）
        for (int i = 0; i < newCardLayoutData.size(); i++) {
            NewsCardLayout layout = newCardLayoutData.get(i);
            if (layout.getType() == NewsAdapter.CARD_TYPE_SINGLE) {
                if (layout.getLeftNewIndex() > deleteNewIndex) {
                    layout.setLeftNewIndex(layout.getLeftNewIndex() - 1);
                }
            } else {
                if (layout.getLeftNewIndex() > deleteNewIndex) {
                    layout.setLeftNewIndex(layout.getLeftNewIndex() - 1);
                }
                if (layout.getRightNewIndex() > deleteNewIndex) {
                    layout.setRightNewIndex(layout.getRightNewIndex() - 1);
                }
            }
        }

        // 将副本赋值给原始数据（关键步骤）
        this.currentNewsData = newNewsData;
        this.currentCardLayoutData = newCardLayoutData;

        // 删除新闻后更新缓存
        cacheManager.saveCache(currentNewsData, currentCardLayoutData);
    }
}