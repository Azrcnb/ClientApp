package com.example.clientapp;
import java.util.List;
import com.example.clientapp.NewsItem; // 修复：添加 NewsItem 的导入

// 正确的接口定义（仅包含抽象方法）
public interface ServerDataCallback {
    void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockNewsCardLayoutData, String successMessage);
    void onFailure(String errorMessage);
}
