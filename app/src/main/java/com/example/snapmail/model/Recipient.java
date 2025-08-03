package com.example.snapmail.model;

public class Recipient {
    private long id;
    private String email;
    private String remark;

    // 构造函数
    public Recipient(String email) {
        this.email = email;
    }

    public Recipient(String email, String remark) {
        this.email = email;
        this.remark = remark;
    }

    public Recipient(long id, String email, String remark) {
        this.id = id;
        this.email = email;
        this.remark = remark;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}