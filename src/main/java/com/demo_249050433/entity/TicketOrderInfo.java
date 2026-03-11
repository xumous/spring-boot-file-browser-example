// TicketOrderInfo.java
package com.demo_249050433.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 火车票订单信息实体类
 * 用于映射数据库中的订单信息表记录
 */
public class TicketOrderInfo {
    private int order_id;
    private String order_no;
    private int stu_no;
    private String train_id;
    private int start_station_id;
    private int end_station_id;
    private int carriage_id;
    private int seat_id;
    private String passenger_name;
    private String passenger_id_card;
    private BigDecimal ticket_price;
    private int order_status;
    private Date order_time;
    private Date pay_time;
    private Date order_expire;
    private String order_status_desc;
    private String train_number;
    private String start_station_name;
    private String end_station_name;
    private String seat_number;
    private String carriage_number;

    // Getter和Setter方法
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getStu_no() {
        return stu_no;
    }

    public void setStu_no(int stu_no) {
        this.stu_no = stu_no;
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public int getStart_station_id() {
        return start_station_id;
    }

    public void setStart_station_id(int start_station_id) {
        this.start_station_id = start_station_id;
    }

    public int getEnd_station_id() {
        return end_station_id;
    }

    public void setEnd_station_id(int end_station_id) {
        this.end_station_id = end_station_id;
    }

    public int getCarriage_id() {
        return carriage_id;
    }

    public void setCarriage_id(int carriage_id) {
        this.carriage_id = carriage_id;
    }

    public int getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public String getPassenger_id_card() {
        return passenger_id_card;
    }

    public void setPassenger_id_card(String passenger_id_card) {
        this.passenger_id_card = passenger_id_card;
    }

    public BigDecimal getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(BigDecimal ticket_price) {
        this.ticket_price = ticket_price;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public Date getPay_time() {
        return pay_time;
    }

    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }

    public Date getOrder_expire() {
        return order_expire;
    }

    public void setOrder_expire(Date order_expire) {
        this.order_expire = order_expire;
    }

    public String getOrder_status_desc() {
        return order_status_desc;
    }

    public void setOrder_status_desc(String order_status_desc) {
        this.order_status_desc = order_status_desc;
    }

    public String getTrain_number() {
        return train_number;
    }

    public void setTrain_number(String train_number) {
        this.train_number = train_number;
    }

    public String getStart_station_name() {
        return start_station_name;
    }

    public void setStart_station_name(String start_station_name) {
        this.start_station_name = start_station_name;
    }

    public String getEnd_station_name() {
        return end_station_name;
    }

    public void setEnd_station_name(String end_station_name) {
        this.end_station_name = end_station_name;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public String getCarriage_number() {
        return carriage_number;
    }

    public void setCarriage_number(String carriage_number) {
        this.carriage_number = carriage_number;
    }

    @Override
    public String toString() {
        return "TicketOrderInfo{" +
                "order_id=" + order_id +
                ", order_no='" + order_no + '\'' +
                ", stu_no=" + stu_no +
                ", train_id='" + train_id + '\'' +
                ", start_station_id=" + start_station_id +
                ", end_station_id=" + end_station_id +
                ", carriage_id=" + carriage_id +
                ", seat_id=" + seat_id +
                ", passenger_name='" + passenger_name + '\'' +
                ", passenger_id_card='" + passenger_id_card + '\'' +
                ", ticket_price=" + ticket_price +
                ", order_status=" + order_status +
                ", order_time=" + order_time +
                ", pay_time=" + pay_time +
                ", order_expire=" + order_expire +
                ", order_status_desc='" + order_status_desc + '\'' +
                ", train_number='" + train_number + '\'' +
                ", start_station_name='" + start_station_name + '\'' +
                ", end_station_name='" + end_station_name + '\'' +
                ", seat_number='" + seat_number + '\'' +
                ", carriage_number='" + carriage_number + '\'' +
                '}';
    }
}