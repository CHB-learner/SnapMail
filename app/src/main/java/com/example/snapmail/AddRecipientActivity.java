package com.example.snapmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.snapmail.model.Recipient;
import com.example.snapmail.util.RecipientDbHelper;

import java.util.regex.Pattern;

public class AddRecipientActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 200;

    // UI组件
    private EditText emailInput;
    private EditText remarkInput;
    private Button btnSave;
    private Button btnCancel;

    // 数据库帮助类
    private RecipientDbHelper dbHelper;

    // 邮箱格式验证正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipient);

        // 初始化数据库帮助类
        dbHelper = new RecipientDbHelper(this);

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
        remarkInput = findViewById(R.id.remark_input);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    /**
     * 设置按钮点击事件
     */
    private void setButtonListeners() {
        // 保存按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipient();
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
     * 保存收件人
     */
    private void saveRecipient() {
        String email = emailInput.getText().toString().trim();
        String remark = remarkInput.getText().toString().trim();

        // 验证邮箱格式
        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证备注
        if (remark.isEmpty()) {
            Toast.makeText(this, "请输入备注", Toast.LENGTH_SHORT).show();
            return;
        }

        // 保存到数据库
        Recipient recipient = new Recipient(email, remark);
        long id = dbHelper.insertRecipient(recipient);

        if (id > 0) {
            Toast.makeText(this, "收件人添加成功", Toast.LENGTH_SHORT).show();
            // 设置结果为成功
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "收件人添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}