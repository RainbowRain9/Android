package com.example.android01;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主界面Activity，包含底部导航栏和三个主要标签页
 */
public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvWelcome;
    
    // 记录当前显示的Fragment
    private Fragment currentFragment;
    
    // 三个主要的Fragment
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // 获取传递过来的用户名
        String username = getIntent().getStringExtra("username");
        
        // 初始化Fragment
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        profileFragment = new ProfileFragment();
        
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
                } else if (itemId == R.id.navigation_message) {
                    selectedFragment = messageFragment;
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = profileFragment;
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
