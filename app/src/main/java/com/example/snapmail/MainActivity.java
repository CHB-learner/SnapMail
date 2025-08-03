package com.example.snapmail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapmail.adapter.RecipientAdapter;
import com.example.snapmail.model.Recipient;
import com.example.snapmail.model.Sender;
import com.example.snapmail.util.EmailSender;
import com.example.snapmail.util.RecipientDbHelper;
import com.example.snapmail.util.SenderDbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 权限请求码
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    // UI组件
    private Button btnTakePhoto;
    private Button btnAddRecipient;
    private Button btnAddSender;
    private RecyclerView recyclerView;
    private TextView noRecipientsText;
    private TextView senderInfoText;

    // 数据和适配器
    private List<Recipient> recipientList;
    private RecipientAdapter recipientAdapter;
    private RecipientDbHelper recipientDbHelper;
    private SenderDbHelper senderDbHelper;

    // 照片相关
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库帮助类
        recipientDbHelper = new RecipientDbHelper(this);
        senderDbHelper = new SenderDbHelper(this);

        // 初始化UI组件
        initUI();

        // 设置按钮点击事件
        setButtonListeners();

        // 检查并请求权限
        checkPermissions();

        // 加载收件人列表
        loadRecipients();
        
        // 更新发件人信息
        updateSenderInfo();
    }

    /**
     * 初始化UI组件
     */
    private void initUI() {
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnAddRecipient = findViewById(R.id.btn_add_recipient);
        btnAddSender = findViewById(R.id.btn_add_sender);
        recyclerView = findViewById(R.id.recipient_list);
        noRecipientsText = findViewById(R.id.no_recipients_text);
        senderInfoText = findViewById(R.id.sender_info_text);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipientList = new ArrayList<>();
        recipientAdapter = new RecipientAdapter(recipientList, this::onDeleteRecipient);
        recyclerView.setAdapter(recipientAdapter);
    }

    /**
     * 设置按钮点击事件
     */
    private void setButtonListeners() {
        // 拍照按钮点击事件
        btnTakePhoto.setOnClickListener(v -> {
            if (checkPermissions()) {
                dispatchTakePictureIntent();
            }
        });

        // 添加收件人按钮点击事件
        btnAddRecipient.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecipientActivity.class);
            startActivityForResult(intent, AddRecipientActivity.REQUEST_CODE);
        });

        // 添加发件人按钮点击事件
        btnAddSender.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSenderActivity.class);
            startActivityForResult(intent, AddSenderActivity.REQUEST_CODE);
        });
    }

    /**
     * 检查并请求权限
     */
    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };

        List<String> permissionToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionToRequest.add(permission);
            }
        }

        if (!permissionToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * 权限请求结果处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "需要相机和存储权限才能正常使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 启动相机拍照
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 创建照片文件
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // 错误处理
                Toast.makeText(this, "创建照片文件失败", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.snapmail.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * 创建照片文件
     */
    private File createImageFile() throws IOException {
        // 创建时间戳文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 目录 */
        );

        // 保存文件路径
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 处理活动结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 处理相机返回结果
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // 照片已保存，直接发送邮件
            Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show();
            sendEmailWithPhoto();
        }

        // 处理添加收件人返回结果
        if (requestCode == AddRecipientActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            // 重新加载收件人列表
            loadRecipients();
        }

        // 处理添加发件人返回结果
        if (requestCode == AddSenderActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            // 更新发件人信息
            updateSenderInfo();
        }
    }

    /**
     * 加载收件人列表
     */
    private void loadRecipients() {
        recipientList.clear();
        recipientList.addAll(recipientDbHelper.getAllRecipients());
        recipientAdapter.notifyDataSetChanged();

        // 显示或隐藏无收件人提示
        if (recipientList.isEmpty()) {
            noRecipientsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noRecipientsText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新发件人信息显示
     */
    private void updateSenderInfo() {
        try {
            Sender defaultSender = senderDbHelper.getDefaultSender();
            if (defaultSender != null && defaultSender.getEmail() != null) {
                senderInfoText.setText("163邮箱发件人: " + defaultSender.getEmail());
                senderInfoText.setVisibility(View.VISIBLE);
            } else {
                senderInfoText.setText("未设置163邮箱发件人");
                senderInfoText.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            senderInfoText.setText("未设置163邮箱发件人");
            senderInfoText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 删除收件人
     */
    private void onDeleteRecipient(Recipient recipient) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.confirm_delete))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    recipientDbHelper.deleteRecipient(recipient.getId());
                    loadRecipients();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    /**
     * 发送带照片的邮件
     */
    private void sendEmailWithPhoto() {
        if (recipientList.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_recipients), Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取默认发件人
        Sender defaultSender = senderDbHelper.getDefaultSender();
        if (defaultSender == null) {
            Toast.makeText(this, "请先添加163邮箱发件人", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取所有收件人邮箱
        List<String> recipients = new ArrayList<>();
        for (Recipient recipient : recipientList) {
            recipients.add(recipient.getEmail());
        }

        // 直接使用内置发送
        sendWithBuiltInEmail(defaultSender, recipients);
    }

    /**
     * 使用系统邮箱发送
     */
    private void sendWithSystemEmail(List<String> recipients) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients.toArray(new String[0]));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "照片分享");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "这是我用SnapMail应用分享的照片");

        // 添加附件
        ArrayList<Uri> uris = new ArrayList<>();
        File fileIn = new File(currentPhotoPath);
        Uri uri = FileProvider.getUriForFile(this, "com.example.snapmail.fileprovider", fileIn);
        uris.add(uri);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        try {
            startActivity(Intent.createChooser(emailIntent, "发送邮件"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.email_send_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 使用内置邮件发送功能
     */
    private void sendWithBuiltInEmail(Sender sender, List<String> recipients) {
        // 显示发送中提示
        Toast.makeText(this, "正在发送邮件...", Toast.LENGTH_SHORT).show();

        // 使用EmailSender发送邮件
        EmailSender.sendEmailWithAttachment(
                sender.getEmail(),
                sender.getPassword(),
                sender.getSmtpHost(),
                sender.getSmtpPort(),
                recipients,
                "照片分享",
                "这是我用SnapMail应用分享的照片",
                currentPhotoPath,
                new EmailSender.SendEmailCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "邮件发送成功", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "邮件发送失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipientDbHelper.close();
        senderDbHelper.close();
    }
}