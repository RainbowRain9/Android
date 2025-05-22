package com.example.android01;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * AI聊天Activity，用于与AI进行对话
 */
public class AIChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnAttach;
    private ImageButton btnVoiceInput;
    private ProgressBar progressAiProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
        
        // 初始化视图
        initViews();
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar_ai_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        recyclerView = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnAttach = findViewById(R.id.btn_attach);
        btnVoiceInput = findViewById(R.id.btn_voice_input);
        progressAiProcessing = findViewById(R.id.progress_ai_processing);
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 发送按钮点击事件
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        
        // 附件按钮点击事件
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AIChatActivity.this, "附件功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 语音输入按钮点击事件
        btnVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AIChatActivity.this, "语音输入功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 发送消息
     */
    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        // 清空输入框
        etMessage.setText("");
        
        // TODO: 实现消息发送逻辑
        
        // 模拟AI处理
        simulateAiProcessing();
    }
    
    /**
     * 模拟AI处理
     */
    private void simulateAiProcessing() {
        // 显示加载指示器
        progressAiProcessing.setVisibility(View.VISIBLE);
        
        // 模拟延迟
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 隐藏加载指示器
                progressAiProcessing.setVisibility(View.GONE);
                
                // TODO: 实现AI回复逻辑
            }
        }, 2000);
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
