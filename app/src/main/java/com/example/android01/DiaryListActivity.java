package com.example.android01;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 日记列表Activity，包含列表、时间线和日历三种视图
 */
public class DiaryListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabViewMode;
    private ViewPager2 viewPager;
    private FloatingActionButton fabNewDiary;
    private ImageButton btnSearch;
    private ImageButton btnFilter;
    
    // 视图模式标签
    private final String[] viewModeTitles = new String[]{"列表", "时间线", "日历"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        
        // 初始化视图
        initViews();
        
        // 设置ViewPager和TabLayout
        setupViewPager();
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar_diary_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        tabViewMode = findViewById(R.id.tab_view_mode);
        viewPager = findViewById(R.id.viewpager_diary_views);
        fabNewDiary = findViewById(R.id.fab_new_diary);
        btnSearch = findViewById(R.id.btn_search);
        btnFilter = findViewById(R.id.btn_filter);
    }
    
    /**
     * 设置ViewPager和TabLayout
     */
    private void setupViewPager() {
        // 设置ViewPager适配器
        DiaryViewPagerAdapter adapter = new DiaryViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // 将TabLayout与ViewPager关联
        new TabLayoutMediator(tabViewMode, viewPager, (tab, position) -> {
            tab.setText(viewModeTitles[position]);
        }).attach();
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 新建日记按钮点击事件
        fabNewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryEditorActivity.class);
                startActivity(intent);
            }
        });
        
        // 搜索按钮点击事件
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 实现搜索功能
            }
        });
        
        // 筛选按钮点击事件
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 实现筛选功能
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * ViewPager适配器，用于管理三种视图的Fragment
     */
    private class DiaryViewPagerAdapter extends FragmentStateAdapter {
        
        public DiaryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // 根据位置返回对应的Fragment
            switch (position) {
                case 0:
                    return new DiaryListFragment();
                case 1:
                    return new DiaryTimelineFragment();
                case 2:
                    return new DiaryCalendarFragment();
                default:
                    return new DiaryListFragment();
            }
        }

        @Override
        public int getItemCount() {
            return viewModeTitles.length;
        }
    }
}
