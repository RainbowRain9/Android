package com.example.android01;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日记日历Fragment，显示日记条目的日历视图
 */
public class DiaryCalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化视图
        calendarView = view.findViewById(R.id.calendar_view);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        recyclerView = view.findViewById(R.id.rv_diary_by_date);
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 设置日历选择监听器
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 更新选中日期显示
                String selectedDate = String.format(Locale.getDefault(), "%d年%d月%d日的日记", year, month + 1, dayOfMonth);
                tvSelectedDate.setText(selectedDate);
                
                // 加载选中日期的日记
                loadDiariesByDate(year, month, dayOfMonth);
            }
        });
        
        // 设置当前日期
        setCurrentDate();
        
        // 加载当前日期的日记
        loadDiariesByCurrentDate();
    }
    
    /**
     * 设置当前日期
     */
    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日的日记", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        tvSelectedDate.setText(currentDate);
    }
    
    /**
     * 加载当前日期的日记
     */
    private void loadDiariesByCurrentDate() {
        // TODO: 实现当前日期日记数据加载逻辑
    }
    
    /**
     * 根据日期加载日记
     */
    private void loadDiariesByDate(int year, int month, int dayOfMonth) {
        // TODO: 实现指定日期日记数据加载逻辑
    }
}
