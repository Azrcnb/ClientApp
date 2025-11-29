package com.example.clientapp.Model;
import java.util.List;

// 正确的接口定义（仅包含抽象方法）
public interface ServerDataCallback {
    void onSuccess(List<NewsItem> mockNewsData, List<NewsCardLayout> mockNewsCardLayoutData, String successMessage);
    void onFailure(String errorMessage);
}
