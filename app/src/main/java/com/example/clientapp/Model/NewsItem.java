package com.example.clientapp.Model;

public class NewsItem {
    private String title;
    private String description;
    private int imageResId;
    private int videoResId = 0; // 新增字段，默认值0

    // 保留原有构造函数（使用默认videoResId=0）
    public NewsItem(String title, String description, int imageResId) {
        this(title, description, imageResId, 0); // 调用四参数构造
    }

    // 新增四参数构造函数（支持外部赋值）
    public NewsItem(String title, String description, int imageResId, int videoResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.videoResId = videoResId; // 保持外部赋值
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    // 新增获取视频资源ID的方法
    public int getVideoResId() {
        return videoResId;
    }
}