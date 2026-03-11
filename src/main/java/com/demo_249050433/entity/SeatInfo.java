// SeatInfo.java
package com.demo_249050433.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 座位信息实体类
 * 用于映射数据库中的座位信息表记录，包含座位状态、价格等信息
 */
public class SeatInfo {
    private int seat_id;
    private int carriage_id;
    private String seat_number;
    private int seat_row;
    private int seat_col;
    private String seat_type;
    private int seat_status;
    private Date lock_time;
    private BigDecimal seat_price;
    private String seat_status_desc;
    private String carriage_number;
    private String carriage_type;

    // Getter和Setter方法
    public int getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }

    public int getCarriage_id() {
        return carriage_id;
    }

    public void setCarriage_id(int carriage_id) {
        this.carriage_id = carriage_id;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public int getSeat_row() {
        return seat_row;
    }

    public void setSeat_row(int seat_row) {
        this.seat_row = seat_row;
    }

    public int getSeat_col() {
        return seat_col;
    }

    public void setSeat_col(int seat_col) {
        this.seat_col = seat_col;
    }

    public String getSeat_type() {
        return seat_type;
    }

    public void setSeat_type(String seat_type) {
        this.seat_type = seat_type;
    }

    public int getSeat_status() {
        return seat_status;
    }

    public void setSeat_status(int seat_status) {
        this.seat_status = seat_status;
    }

    public Date getLock_time() {
        return lock_time;
    }

    public void setLock_time(Date lock_time) {
        this.lock_time = lock_time;
    }

    public BigDecimal getSeat_price() {
        return seat_price;
    }

    public void setSeat_price(BigDecimal seat_price) {
        this.seat_price = seat_price;
    }

    public String getSeat_status_desc() {
        return seat_status_desc;
    }

    public void setSeat_status_desc(String seat_status_desc) {
        this.seat_status_desc = seat_status_desc;
    }

    public String getCarriage_number() {
        return carriage_number;
    }

    public void setCarriage_number(String carriage_number) {
        this.carriage_number = carriage_number;
    }

    public String getCarriage_type() {
        return carriage_type;
    }

    public void setCarriage_type(String carriage_type) {
        this.carriage_type = carriage_type;
    }

    @Override
    public String toString() {
        return "SeatInfo{" +
                "seat_id=" + seat_id +
                ", carriage_id=" + carriage_id +
                ", seat_number='" + seat_number + '\'' +
                ", seat_row=" + seat_row +
                ", seat_col=" + seat_col +
                ", seat_type='" + seat_type + '\'' +
                ", seat_status=" + seat_status +
                ", lock_time=" + lock_time +
                ", seat_price=" + seat_price +
                ", seat_status_desc='" + seat_status_desc + '\'' +
                ", carriage_number='" + carriage_number + '\'' +
                ", carriage_type='" + carriage_type + '\'' +
                '}';
    }
}