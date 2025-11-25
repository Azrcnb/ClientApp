package com.example.clientapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class TestToolActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_tool);

        // 获取曝光事件
        List<String> exposureEvents = getIntent().getStringArrayListExtra("EXPOSURE_EVENTS");
        if (exposureEvents == null) {
            exposureEvents = new ArrayList<>();
        }

        // 获取事件日志容器
        LinearLayout eventLogContainer = findViewById(R.id.event_log_container);
        eventLogContainer.removeAllViews(); // 清空现有内容

        // 添加标题
        TextView header = new TextView(this);
        header.setText("卡片曝光事件日志");
        header.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        header.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        header.setPadding(0, 0, 0, 8);
        eventLogContainer.addView(header);

        // 添加每个事件
        for (String event : exposureEvents) {
            TextView logItem = new TextView(this);
            logItem.setText(event);
            logItem.setBackgroundColor(Color.parseColor("#F5F5F5"));
            logItem.setPadding(12, 12, 12, 12);
            logItem.setTextColor(Color.parseColor("#333333"));
            eventLogContainer.addView(logItem);
        }
    }
}