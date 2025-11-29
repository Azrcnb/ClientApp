package com.example.clientapp.Model;

public class NewsCardLayout {
    public static final int CARD_TYPE_SINGLE = 0; // 单列布局
    public static final int CARD_TYPE_DOUBLE = 1; // 双列布局

    private int type;
    private int leftNewIndex; // 单列时：新闻索引；双列时：第一条新闻的索引
    private int rightNewIndex; // 双列时：第二条新闻的索引（单列时无效）

    public NewsCardLayout(int type, int leftNewIndex) {
        this.type = type;
        this.leftNewIndex = leftNewIndex;
        this.rightNewIndex = -1; // 无效值
    }

    public NewsCardLayout(int type, int leftNewIndex, int rightNewIndex) {
        this.type = type;
        this.leftNewIndex = leftNewIndex;
        this.rightNewIndex = rightNewIndex;
    }

    public int getType() {
        return type;
    }

    public int getLeftNewIndex() {
        return leftNewIndex;
    }

    public int getRightNewIndex() {
        return rightNewIndex;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setLeftNewIndex(int leftNewIndex) {
        this.leftNewIndex = leftNewIndex;
    }
    public void setRightNewIndex(int rightNewIndex) {
        this.rightNewIndex = rightNewIndex;
    }


}