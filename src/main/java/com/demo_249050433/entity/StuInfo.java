// StuInfo.java
package com.demo_249050433.entity;

/**
 * 学生信息实体类
 * 用于映射数据库中的学生信息表记录，用于登录注册功能
 */
public class StuInfo {
    private int stu_no;
    private String stu_name;
    private String stu_pwd;
    private String stu_telephone;
    private int stu_status;

    // Getter和Setter方法
    public int getStu_no() {
        return stu_no;
    }

    public void setStu_no(int stu_no) {
        this.stu_no = stu_no;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getStu_pwd() {
        return stu_pwd;
    }

    public void setStu_pwd(String stu_pwd) {
        this.stu_pwd = stu_pwd;
    }

    public String getStu_telephone() {
        return stu_telephone;
    }

    public void setStu_telephone(String stu_telephone) {
        this.stu_telephone = stu_telephone;
    }

    public int getStu_status() {
        return stu_status;
    }

    public void setStu_status(int stu_status) {
        this.stu_status = stu_status;
    }

    @Override
    public String toString() {
        return "StuInfo{" +
                "stu_no=" + stu_no +
                ", stu_name='" + stu_name + '\'' +
                ", stu_pwd='" + stu_pwd + '\'' +
                ", stu_telephone='" + stu_telephone + '\'' +
                ", stu_status=" + stu_status +
                '}';
    }
}