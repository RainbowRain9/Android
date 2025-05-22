package com.example.android01;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主界面Activity，包含底部导航栏和主页标签页
 */
public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvWelcome;

    // 记录当前显示的Fragment
    private Fragment currentFragment;

    // 主要的Fragment
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 获取传递过来的用户名
        String username = getIntent().getStringExtra("username");

        // 初始化Fragment
        homeFragment = new HomeFragment();

        // 设置欢迎信息
        tvWelcome = findViewById(R.id.tv_welcome);
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("欢迎您，" + username + "！");
        }

        // 默认显示首页Fragment
        currentFragment = homeFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();

        // 设置底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = homeFragment;
                } else if (itemId == R.id.navigation_diary) {
                    // 启动日记列表Activity
                    Intent intent = new Intent(HomeActivity.this, DiaryListActivity.class);
                    startActivity(intent);
                    return false;
                } else if (itemId == R.id.navigation_list) {
                    // TODO: 实现清单功能
                    Toast.makeText(HomeActivity.this, "清单功能尚未实现", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.navigation_ai_chat) {
                    // 启动AI聊天Activity
                    Intent intent = new Intent(HomeActivity.this, AIChatActivity.class);
                    startActivity(intent);
                    return false;
                } else if (itemId == R.id.navigation_settings) {
                    // 启动设置Activity
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return false;
                }

                if (selectedFragment != null && selectedFragment != currentFragment) {
                    // 切换Fragment
                    currentFragment = selectedFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, currentFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}
