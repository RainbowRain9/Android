package com.example.android01;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日记编辑器Activity，用于创建和编辑日记条目
 */
public class DiaryEditorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvDiaryDate;
    private EditText etDiaryTitle;
    private EditText etDiaryContent;
    private TabLayout tabDiaryType;
    private FloatingActionButton fabSaveDiary;
    
    // AI辅助按钮
    private Button btnAiGenerate;
    private Button btnAiImprove;
    private Button btnAiSummarize;
    private Button btnAiTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_editor);
        
        // 初始化视图
        initViews();
        
        // 设置当前日期
        setCurrentDate();
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar_diary_editor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        tvDiaryDate = findViewById(R.id.tv_diary_date);
        etDiaryTitle = findViewById(R.id.et_diary_title);
        etDiaryContent = findViewById(R.id.et_diary_content);
        tabDiaryType = findViewById(R.id.tab_diary_type);
        fabSaveDiary = findViewById(R.id.fab_save_diary);
        
        // AI辅助按钮
        btnAiGenerate = findViewById(R.id.btn_ai_generate);
        btnAiImprove = findViewById(R.id.btn_ai_improve);
        btnAiSummarize = findViewById(R.id.btn_ai_summarize);
        btnAiTemplate = findViewById(R.id.btn_ai_template);
        
        // 富文本编辑器工具栏按钮
        ImageButton btnFormatBold = findViewById(R.id.btn_format_bold);
        ImageButton btnFormatItalic = findViewById(R.id.btn_format_italic);
        ImageButton btnFormatUnderline = findViewById(R.id.btn_format_underline);
        ImageButton btnFormatBulletList = findViewById(R.id.btn_format_bullet_list);
        ImageButton btnFormatNumberedList = findViewById(R.id.btn_format_numbered_list);
        ImageButton btnInsertImage = findViewById(R.id.btn_insert_image);
        ImageButton btnInsertLink = findViewById(R.id.btn_insert_link);
    }
    
    /**
     * 设置当前日期
     */
    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        tvDiaryDate.setText(currentDate);
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 保存按钮点击事件
        fabSaveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary();
            }
        });
        
        // 日记类型选项卡选择事件
        tabDiaryType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 根据选择的日记类型更新UI或提供模板
                String diaryType = tab.getText().toString();
                Toast.makeText(DiaryEditorActivity.this, "已选择: " + diaryType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 不需要处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不需要处理
            }
        });
        
        // AI辅助按钮点击事件
        btnAiGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DiaryEditorActivity.this, "AI内容生成功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAiImprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DiaryEditorActivity.this, "AI内容改进功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAiSummarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DiaryEditorActivity.this, "AI摘要生成功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAiTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DiaryEditorActivity.this, "AI模板功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 保存日记
     */
    private void saveDiary() {
        String title = etDiaryTitle.getText().toString().trim();
        String content = etDiaryContent.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: 实现日记保存逻辑
        
        Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
