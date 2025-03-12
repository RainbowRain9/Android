package com.example.android01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android01.data.AppDatabase;
import com.example.android01.data.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button btnLogin, btnRegister;
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 初始化数据库和线程池
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // 绑定控件
        editTextUsername = findViewById(R.id.username_input);
        editTextPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // 设置登录按钮点击事件
        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 输入验证
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 处理登录
            handleLogin(username, password);
        });

        // 设置注册按钮点击事件
        btnRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 输入验证
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 处理注册
            handleRegister(username, password);
        });
    }

    // 处理登录逻辑
    private void handleLogin(String username, String password) {
        executorService.execute(() -> {
            // 在子线程中查询数据库
            User user = db.userDao().getUser(username, password);
            
            // 在主线程中更新UI
            runOnUiThread(() -> {
                if (user != null) {
                    Toast.makeText(MainActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                    // TODO: 这里可以跳转到主页面
                    clearInputs();
                } else {
                    Toast.makeText(MainActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 处理注册逻辑
    private void handleRegister(String username, String password) {
        executorService.execute(() -> {
            // 检查用户名是否已存在
            User existingUser = db.userDao().getUserByUsername(username);
            
            if (existingUser != null) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // 创建新用户
            User newUser = new User(username, password);
            db.userDao().insert(newUser);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                clearInputs();
            });
        });
    }

    // 清空输入框
    private void clearInputs() {
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}