package com.example.android01;

// 导入所需的Android和Java类
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android01.data.AppDatabase;
import com.example.android01.data.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主活动类，处理用户登录和注册功能
 */
public class MainActivity extends AppCompatActivity {
    // UI组件
    private EditText editTextUsername, editTextPassword;
    private Button btnLogin, btnRegister;
    // 数据库实例
    private AppDatabase db;
    // 用于执行异步操作的线程池
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置活动的布局为login.xml
        setContentView(R.layout.login);

        // 初始化数据库和线程池
        db = AppDatabase.getInstance(this);
        // 创建单线程的执行器服务，确保数据库操作按顺序执行
        executorService = Executors.newSingleThreadExecutor();

        // 绑定UI控件
        editTextUsername = findViewById(R.id.username_input);
        editTextPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // 设置登录按钮点击事件监听器
        btnLogin.setOnClickListener(v -> {
            // 获取用户输入并去除首尾空格
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 验证输入是否为空
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 调用登录处理方法
            handleLogin(username, password);
        });

        // 设置注册按钮点击事件监听器
        btnRegister.setOnClickListener(v -> {
            // 获取用户输入并去除首尾空格
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 验证输入是否为空
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 调用注册处理方法
            handleRegister(username, password);
        });
    }

    /**
     * 处理用户登录逻辑
     * @param username 用户名
     * @param password 密码
     */
    private void handleLogin(String username, String password) {
        executorService.execute(() -> {
            // 在后台线程中查询数据库验证用户信息
            User user = db.userDao().getUser(username, password);

            // 在主线程中处理登录结果并更新UI
            runOnUiThread(() -> {
                if (user != null) {
                    // 登录成功，显示提示信息
                    Toast.makeText(MainActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();

                    // 创建跳转到主界面的Intent
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    
                    // 将用户名传递给主界面
                    intent.putExtra("username", username);
                    
                    // 启动主界面
                    startActivity(intent);
                    
                    // 结束当前Activity，防止用户按返回键回到登录界面
                    finish();
                    
                    // 清空输入框
                    clearInputs();
                } else {
                    // 登录失败，显示错误信息
                    Toast.makeText(MainActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * 处理用户注册逻辑
     * @param username 用户名
     * @param password 密码
     */
    private void handleRegister(String username, String password) {
        executorService.execute(() -> {
            // 在后台线程中检查用户名是否已存在
            User existingUser = db.userDao().getUserByUsername(username);

            if (existingUser != null) {
                // 用户名已存在，在主线程中显示错误信息
                runOnUiThread(() ->
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // 创建新用户对象并插入数据库
            User newUser = new User(username, password);
            db.userDao().insert(newUser);

            // 在主线程中显示注册成功信息并清空输入框
            runOnUiThread(() -> {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                clearInputs();
            });
        });
    }

    /**
     * 清空用户名和密码输入框
     */
    private void clearInputs() {
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 活动销毁时关闭线程池
        executorService.shutdown();
    }
}
