package com.example.android01;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 首页Fragment，显示仪表盘内容
 */
public class HomeFragment extends Fragment {

    private Button btnNewDiary;
    private Button btnNewList;
    private Button btnAiChat;
    private ImageButton btnRefreshInspiration;
    private TextView tvAiInspiration;
    private TextView tvViewAllDiaries;
    private TextView tvViewAllLists;
    private RecyclerView rvRecentDiaries;
    private RecyclerView rvRecentLists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载Fragment布局
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化视图组件
        initViews(view);
        
        // 设置事件监听器
        setupListeners();
        
        // 加载数据
        loadData();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews(View view) {
        btnNewDiary = view.findViewById(R.id.btn_new_diary);
        btnNewList = view.findViewById(R.id.btn_new_list);
        btnAiChat = view.findViewById(R.id.btn_ai_chat);
        btnRefreshInspiration = view.findViewById(R.id.btn_refresh_inspiration);
        tvAiInspiration = view.findViewById(R.id.tv_ai_inspiration);
        tvViewAllDiaries = view.findViewById(R.id.tv_view_all_diaries);
        tvViewAllLists = view.findViewById(R.id.tv_view_all_lists);
        rvRecentDiaries = view.findViewById(R.id.rv_recent_diaries);
        rvRecentLists = view.findViewById(R.id.rv_recent_lists);
        
        // 设置RecyclerView
        rvRecentDiaries.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentLists.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    
    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // 写新日记按钮点击事件
        btnNewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryEditorActivity.class);
                startActivity(intent);
            }
        });
        
        // 新建清单按钮点击事件
        btnNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "清单功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
        
        // AI对话按钮点击事件
        btnAiChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AIChatActivity.class);
                startActivity(intent);
            }
        });
        
        // 刷新灵感按钮点击事件
        btnRefreshInspiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAiInspiration();
            }
        });
        
        // 查看全部日记点击事件
        tvViewAllDiaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryListActivity.class);
                startActivity(intent);
            }
        });
        
        // 查看全部清单点击事件
        tvViewAllLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "清单列表功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 加载数据
     */
    private void loadData() {
        // 加载AI灵感
        loadAiInspiration();
        
        // 加载最近日记
        loadRecentDiaries();
        
        // 加载最近清单
        loadRecentLists();
    }
    
    /**
     * 加载AI灵感
     */
    private void loadAiInspiration() {
        // TODO: 实现从API加载AI灵感的逻辑
        String[] inspirations = {
            "今天是新的一天，记录下你的所思所想吧！",
            "回顾一下今天的收获，有什么让你感到开心的事情吗？",
            "写下三件你今天感恩的事情，培养积极的心态。",
            "今天遇到了什么挑战？你是如何应对的？",
            "记录下今天的一个小确幸，让美好永远保存。"
        };
        
        // 随机选择一条灵感
        int randomIndex = (int) (Math.random() * inspirations.length);
        tvAiInspiration.setText(inspirations[randomIndex]);
    }
    
    /**
     * 刷新AI灵感
     */
    private void refreshAiInspiration() {
        // 简单地重新加载灵感
        loadAiInspiration();
        
        // 显示提示
        Toast.makeText(getContext(), "已刷新灵感", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 加载最近日记
     */
    private void loadRecentDiaries() {
        // TODO: 实现从数据库加载最近日记的逻辑
    }
    
    /**
     * 加载最近清单
     */
    private void loadRecentLists() {
        // TODO: 实现从数据库加载最近清单的逻辑
    }
}
