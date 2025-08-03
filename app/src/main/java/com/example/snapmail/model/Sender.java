package com.example.snapmail.model;

public class Sender {
    private long id;
    private String email;
    private String password;
    private String smtpHost;
    private int smtpPort;
    private boolean isDefault;

    // 构造函数
    public Sender(String email, String password, String smtpHost, int smtpPort) {
        this.email = email;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.isDefault = false;
    }

    public Sender(long id, String email, String password, String smtpHost, int smtpPort, boolean isDefault) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.isDefault = isDefault;
    }

    // Getter和Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
} 