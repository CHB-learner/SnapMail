package com.example.snapmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.snapmail.model.Sender;
import com.example.snapmail.util.SenderDbHelper;
import com.example.snapmail.util.EmailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AddSenderActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 300;

    // UI组件
    private EditText emailInput;
    private EditText passwordInput;
    private Button btnSave;
    private Button btnCancel;

    // 数据库帮助类
    private SenderDbHelper dbHelper;

    // 邮箱格式验证正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@163\\.com$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender);

        // 初始化数据库帮助类
        dbHelper = new SenderDbHelper(this);

        // 初始化UI组件
        initUI();

        // 设置按钮点击事件
        setButtonListeners();
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // 设置默认163邮箱提示
        emailInput.setHint("请输入163邮箱地址");
    }

    /**
     * 设置按钮点击事件
     */
    private void setButtonListeners() {
        // 保存按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSender();
            }
        });

        // 取消按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 保存发件人
     */
    private void saveSender() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // 验证输入
        if (email.isEmpty()) {
            Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "请输入有效的163邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 163邮箱的SMTP配置
        String smtpHost = "smtp.163.com";
        int smtpPort = 465; // 使用SSL端口

        // 保存到数据库
        Sender sender = new Sender(email, password, smtpHost, smtpPort);
        long id = dbHelper.insertSender(sender);

        if (id > 0) {
            // 设置为默认发件人
            dbHelper.setDefaultSender(id);
            
            Toast.makeText(this, "163邮箱发件人添加成功", Toast.LENGTH_SHORT).show();
            
            // 发送测试邮件
            sendTestEmail(sender);
            
            // 设置结果为成功
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "发件人添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证163邮箱格式
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 发送测试邮件
     */
    private void sendTestEmail(Sender sender) {
        List<String> recipients = new ArrayList<>();
        recipients.add(sender.getEmail()); // 发给自己

        EmailSender.sendEmailWithAttachment(
                sender.getEmail(),
                sender.getPassword(),
                sender.getSmtpHost(),
                sender.getSmtpPort(),
                recipients,
                "SnapMail - 邮箱绑定成功",
                "恭喜！您的163邮箱已成功绑定到SnapMail应用。\n\n现在您可以：\n1. 添加收件人\n2. 拍照并自动发送邮件\n\n感谢使用SnapMail！",
                null, // 无附件
                new EmailSender.SendEmailCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(AddSenderActivity.this, "测试邮件发送成功", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddSenderActivity.this, "测试邮件发送失败: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
} 