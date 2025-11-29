package com.example.clientapp.ListenerManager;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 事件委托优化版的双列触摸监听器
 * 性能优势：单一监听器处理所有卡片事件，内存开销极低
 */
public class DualColumnTouchListener implements RecyclerView.OnItemTouchListener {

    public interface OnColumnLongClickListener {
        void onColumnLongClick(int Position, boolean isLeftColumn);
    }

    private final GestureDetector gestureDetector;
    private final OnColumnLongClickListener longClickListener;
    private final RecyclerView recyclerView;

    public DualColumnTouchListener(Context context, RecyclerView recyclerView,
                                   OnColumnLongClickListener listener) {
        this.recyclerView = recyclerView;
        this.longClickListener = listener;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                handleLongPress(e);
            }
        });
    }

    private void handleLongPress(MotionEvent e) {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childView == null || longClickListener == null) return;

        // 获取点击的卡片位置
        int position = recyclerView.getChildAdapterPosition(childView);
        if (position == RecyclerView.NO_POSITION) return;

        // ✅ 关键修改：确定点击的是左列还是右列
        int childViewLeft = childView.getLeft();
        int childViewRight = childView.getRight();
        int childViewWidth = childViewRight - childViewLeft;
        // 计算点击位置相对于 childView 的水平坐标
        float clickX = e.getX() - childViewLeft;
        // 判断点击位置是否在 childView 的左半边
        boolean isLeftColumn = (clickX < childViewWidth / 2);

        // 调用监听器
        longClickListener.onColumnLongClick(position, isLeftColumn);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false; // 不拦截事件，让 RecyclerView 正常处理滚动
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}