# SnapMail - 拍照邮件发送应用

## 📱 项目简介

SnapMail 是一款专为163邮箱用户设计的Android应用，支持拍照后自动发送邮件。应用采用简洁的界面设计，提供便捷的拍照-发送一体化体验。

## 使用文档
https://t18y7sz3iu.feishu.cn/wiki/J5hvwwRScitRyIk3yd1cofV8nod?from=from_copylink


## ✨ 主要功能

### 📸 拍照发送
- **一键拍照**：点击拍照按钮即可启动相机
- **自动发送**：拍完照后自动通过163邮箱发送邮件
- **照片附件**：拍摄的照片自动作为邮件附件发送

### 📧 163邮箱专用
- **专门优化**：针对163邮箱进行优化配置
- **SSL加密**：使用SSL端口465确保邮件安全
- **授权码支持**：使用163邮箱授权码而非登录密码

### 👥 收件人管理
- **添加收件人**：支持添加多个收件人邮箱
- **备注功能**：为每个收件人添加备注，方便区分
- **收件人列表**：清晰显示邮箱和备注信息
- **删除功能**：支持删除不需要的收件人

### 🔧 发件人管理
- **163邮箱绑定**：专门支持163邮箱发件人
- **自动测试**：绑定成功后自动发送测试邮件
- **默认发件人**：自动设置为默认发件人

## 🛠️ 技术特性

### 架构设计
- **MVVM架构**：采用Model-View-ViewModel架构模式
- **数据库存储**：使用SQLite本地数据库存储收件人和发件人信息
- **异步处理**：邮件发送采用异步处理，不阻塞UI

### 权限管理
- **相机权限**：自动申请相机权限用于拍照
- **存储权限**：申请存储权限用于保存照片
- **网络权限**：申请网络权限用于发送邮件

### 邮件发送
- **SMTP协议**：使用JavaMail库实现SMTP邮件发送
- **SSL加密**：支持SSL加密的邮件传输
- **附件支持**：支持照片作为邮件附件发送

## 📋 系统要求

- **Android版本**：Android 6.0 (API 24) 及以上
- **网络连接**：需要网络连接用于发送邮件
- **相机硬件**：需要设备具备相机功能
- **存储空间**：需要存储空间用于保存照片

## 🚀 安装使用

### 1. 安装应用
```bash
# 下载APK文件
app/build/outputs/apk/debug/app-debug.apk
```

### 2. 首次设置
1. **添加163邮箱发件人**
   - 点击"添加163邮箱"按钮
   - 输入163邮箱地址
   - 输入163邮箱授权码（不是登录密码）
   - 系统会自动配置SMTP服务器

2. **添加收件人**
   - 点击"添加收件人"按钮
   - 输入收件人邮箱地址
   - 输入备注信息（必填）
   - 点击保存

### 3. 使用流程
1. **拍照发送**：点击"拍照"按钮 → 拍照 → 自动发送邮件
2. **管理收件人**：在主界面查看和管理收件人列表
3. **管理发件人**：在主界面查看当前发件人信息

## 🔧 163邮箱配置说明

### 授权码获取步骤
1. 登录163邮箱网页版
2. 进入设置 → POP3/SMTP/IMAP
3. 开启SMTP服务
4. 获取授权码（不是登录密码）

### SMTP配置
- **服务器**：smtp.163.com
- **端口**：465
- **加密**：SSL
- **认证**：需要

## 📁 项目结构

```
app/
├── src/main/
│   ├── java/com/example/snapmail/
│   │   ├── MainActivity.java              # 主界面
│   │   ├── AddRecipientActivity.java      # 添加收件人
│   │   ├── AddSenderActivity.java         # 添加发件人
│   │   ├── adapter/
│   │   │   └── RecipientAdapter.java      # 收件人列表适配器
│   │   ├── model/
│   │   │   ├── Recipient.java            # 收件人数据模型
│   │   │   └── Sender.java               # 发件人数据模型
│   │   └── util/
│   │       ├── EmailSender.java          # 邮件发送工具
│   │       ├── RecipientDbHelper.java    # 收件人数据库
│   │       └── SenderDbHelper.java       # 发件人数据库
│   └── res/
│       ├── layout/                        # 界面布局文件
│       ├── values/                        # 资源文件
│       └── xml/                          # XML配置文件
```

## 🛠️ 开发环境

### 环境要求
- **Android Studio**：最新版本
- **Java版本**：Java 8 或更高
- **Gradle版本**：7.6.1
- **编译SDK**：Android API 33

### 构建步骤
```bash
# 克隆项目
git clone [项目地址]

# 进入项目目录
cd SnapMail

# 构建项目
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 📦 依赖库

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // 邮件发送库
    implementation 'com.sun.mail:android-mail:1.6.7'
    implementation 'com.sun.mail:android-activation:1.6.7'
}
```

## 🔒 隐私说明

- **本地存储**：所有数据存储在设备本地SQLite数据库中
- **网络传输**：仅用于发送邮件，不收集用户数据
- **权限使用**：仅使用必要的相机、存储和网络权限

## 🐛 常见问题

### Q: 为什么只能使用163邮箱？
A: 本应用专门针对163邮箱进行了优化，确保最佳的兼容性和稳定性。

### Q: 如何获取163邮箱授权码？
A: 登录163邮箱网页版 → 设置 → POP3/SMTP/IMAP → 开启SMTP服务 → 获取授权码。

### Q: 拍照后没有自动发送邮件？
A: 请检查是否已添加发件人和收件人，以及网络连接是否正常。

### Q: 邮件发送失败怎么办？
A: 请检查163邮箱授权码是否正确，以及网络连接是否正常。

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 GitHub Issue
- 发送邮件至：[联系邮箱]

---

**SnapMail** - 让拍照发送邮件变得简单高效！
