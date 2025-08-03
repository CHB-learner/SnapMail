package com.example.snapmail.util;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;

public class EmailSender {

    private static final String TAG = "EmailSender";

    // 邮件发送结果回调接口
    public interface SendEmailCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * 发送带附件的邮件
     */
    public static void sendEmailWithAttachment(String senderEmail, String senderPassword,
                                              String smtpHost, int smtpPort,
                                              List<String> recipients, String subject,
                                              String body, String filePath,
                                              SendEmailCallback callback) {

        new SendEmailTask(senderEmail, senderPassword, smtpHost, smtpPort,
                recipients, subject, body, filePath, callback).execute();
    }

    /**
     * 异步发送邮件任务
     */
    private static class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String senderEmail;
        private String senderPassword;
        private String smtpHost;
        private int smtpPort;
        private List<String> recipients;
        private String subject;
        private String body;
        private String filePath;
        private SendEmailCallback callback;
        private String errorMessage;

        public SendEmailTask(String senderEmail, String senderPassword, String smtpHost, int smtpPort,
                            List<String> recipients, String subject, String body, String filePath,
                            SendEmailCallback callback) {
            this.senderEmail = senderEmail;
            this.senderPassword = senderPassword;
            this.smtpHost = smtpHost;
            this.smtpPort = smtpPort;
            this.recipients = recipients;
            this.subject = subject;
            this.body = body;
            this.filePath = filePath;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // 配置邮件属性
                Properties props = new Properties();
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", smtpPort);
                props.put("mail.smtp.auth", "true");
                
                // 163邮箱使用SSL
                if (smtpPort == 465) {
                    props.put("mail.smtp.socketFactory.port", smtpPort);
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.socketFactory.fallback", "false");
                } else {
                    props.put("mail.smtp.starttls.enable", "true");
                }

                // 创建会话
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                // 设置调试模式（可选）
                session.setDebug(true);

                // 创建邮件消息
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));

                // 设置收件人
                InternetAddress[] recipientAddresses = new InternetAddress[recipients.size()];
                for (int i = 0; i < recipients.size(); i++) {
                    recipientAddresses[i] = new InternetAddress(recipients.get(i));
                }
                message.setRecipients(Message.RecipientType.TO, recipientAddresses);

                // 设置主题
                message.setSubject(subject);

                // 创建多部分消息
                MimeMultipart multipart = new MimeMultipart();

                // 邮件正文部分
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body, "UTF-8");
                multipart.addBodyPart(textPart);

                // 添加附件
                if (filePath != null && !filePath.isEmpty()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    try {
                        File file = new File(filePath);
                        attachmentPart.attachFile(file);
                        attachmentPart.setFileName(MimeUtility.encodeText(file.getName(), "UTF-8", "B"));
                        multipart.addBodyPart(attachmentPart);
                    } catch (IOException e) {
                        Log.e(TAG, "添加附件失败: " + e.getMessage());
                        errorMessage = "添加附件失败: " + e.getMessage();
                        return false;
                    }
                }

                // 设置邮件内容
                message.setContent(multipart);

                // 发送邮件
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                Log.e(TAG, "发送邮件失败: " + e.getMessage());
                errorMessage = "发送邮件失败: " + e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(errorMessage);
                }
            }
        }
    }
}