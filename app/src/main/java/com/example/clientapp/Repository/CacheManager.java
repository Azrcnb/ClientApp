package com.example.clientapp.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log; // 添加日志导入
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.example.clientapp.Model.NewsCardLayout;
import com.example.clientapp.Model.NewsItem;

/** * 专门的缓存管理类 */
public class CacheManager {
    private static final String PREF_NAME = "NewsCache";
    private static final String KEY_NEWS_DATA = "cached_news_data";
    private static final String KEY_CARD_DATA = "cached_card_data";
    private static final String KEY_CACHE_TIME = "cache_time";
    private static final long CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000; // 24小时
    private final SharedPreferences prefs;
    private final Gson gson;

    public CacheManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveCache(List<NewsItem> newsData, List<NewsCardLayout> cardData) {
        try {
            String newsJson = gson.toJson(newsData);
            String cardJson = gson.toJson(cardData);
            prefs.edit()
                    .putString(KEY_NEWS_DATA, newsJson)
                    .putString(KEY_CARD_DATA, cardJson)
                    .putLong(KEY_CACHE_TIME, System.currentTimeMillis())
                    .apply();
        } catch (Exception e) {
            Log.e("CacheManager", "Error saving cache data", e); // 替换为 Log.e
        }
    }

    public CacheResult loadCache() {
        try {
            long cacheTime = prefs.getLong(KEY_CACHE_TIME, 0);
            if (System.currentTimeMillis() - cacheTime > CACHE_EXPIRY_MS) {
                clearCache();
                return null;
            }
            String newsJson = prefs.getString(KEY_NEWS_DATA, null);
            String cardJson = prefs.getString(KEY_CARD_DATA, null);
            if (newsJson == null || cardJson == null) {
                return null;
            }
            Type newsType = new TypeToken<List<NewsItem>>(){}.getType();
            Type cardType = new TypeToken<List<NewsCardLayout>>(){}.getType();
            List<NewsItem> newsData = gson.fromJson(newsJson, newsType);
            List<NewsCardLayout> cardData = gson.fromJson(cardJson, cardType);
            if (newsData == null || newsData.isEmpty()) {
                return null;
            }
            return new CacheResult(newsData, cardData);
        } catch (Exception e) {
            Log.e("CacheManager", "Error loading cache data", e); // 替换为 Log.e
            return null;
        }
    }

    public void clearCache() {
        prefs.edit().clear().apply();
    }


    public static class CacheResult {
        public final List<NewsItem> newsData;
        public final List<NewsCardLayout> cardData;

        public CacheResult(List<NewsItem> newsData, List<NewsCardLayout> cardData) {
            this.newsData = new ArrayList<>(newsData);
            this.cardData = new ArrayList<>(cardData);
        }
    }
}