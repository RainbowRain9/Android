package com.example.android01;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

/**
 * 设置Activity，用于管理应用设置
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout llProfile;
    private LinearLayout llChangePassword;
    private LinearLayout llLogout;
    private LinearLayout llApiKey;
    private LinearLayout llPrivacyPolicy;
    private RadioGroup rgTheme;
    private RadioButton rbThemeLight;
    private RadioButton rbThemeDark;
    private RadioButton rbThemeSystem;
    private SeekBar seekbarFontSize;
    private Spinner spinnerAiModel;
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化视图
        initViews();

        // 设置监听器
        setupListeners();

        // 加载设置
        loadSettings();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llProfile = findViewById(R.id.ll_profile);
        llChangePassword = findViewById(R.id.ll_change_password);
        llLogout = findViewById(R.id.ll_logout);
        llApiKey = findViewById(R.id.ll_api_key);
        llPrivacyPolicy = findViewById(R.id.ll_privacy_policy);
        rgTheme = findViewById(R.id.rg_theme);
        rbThemeLight = findViewById(R.id.rb_theme_light);
        rbThemeDark = findViewById(R.id.rb_theme_dark);
        rbThemeSystem = findViewById(R.id.rb_theme_system);
        seekbarFontSize = findViewById(R.id.seekbar_font_size);
        spinnerAiModel = findViewById(R.id.spinner_ai_model);
        tvAppVersion = findViewById(R.id.tv_app_version);

        // 设置应用版本
        tvAppVersion.setText("1.0.0"); // 硬编码版本号

        // 设置AI模型下拉列表
        String[] aiModels = {"GPT-3.5", "GPT-4", "Claude Sonnet", "自定义API"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, aiModels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAiModel.setAdapter(adapter);
    }

    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 个人资料点击事件
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "个人资料功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        // 修改密码点击事件
        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "修改密码功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        // 登出账户点击事件
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 实现登出逻辑
                Toast.makeText(SettingsActivity.this, "已登出", Toast.LENGTH_SHORT).show();

                // 返回登录页面
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // API密钥管理点击事件
        llApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "API密钥管理功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        // 隐私政策点击事件
        llPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "隐私政策功能尚未实现", Toast.LENGTH_SHORT).show();
            }
        });

        // 主题选择事件
        rgTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_theme_light) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.rb_theme_dark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (checkedId == R.id.rb_theme_system) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }
        });

        // 字体大小调整事件
        seekbarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO: 实现字体大小调整逻辑
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 不需要处理
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 不需要处理
            }
        });
    }

    /**
     * 加载设置
     */
    private void loadSettings() {
        // 加载主题设置
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            rbThemeLight.setChecked(true);
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            rbThemeDark.setChecked(true);
        } else {
            rbThemeSystem.setChecked(true);
        }

        // TODO: 加载其他设置
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
