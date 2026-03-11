// TrainInfo.java
package com.demo_249050433.entity;

import java.sql.Time;

/**
 * 高铁车次信息实体类
 * 用于映射数据库中的车次信息表记录
 */
public class TrainInfo {
    private String train_id;
    private String train_number;
    private String train_type;
    private int start_station_id;
    private int end_station_id;
    private Time departure_time;
    private Time arrival_time;
    private String run_time;
    private int train_status;
    private String start_station_name;
    private String end_station_name;

    // Getter和Setter方法
    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public String getTrain_number() {
        return train_number;
    }

    public void setTrain_number(String train_number) {
        this.train_number = train_number;
    }

    public String getTrain_type() {
        return train_type;
    }

    public void setTrain_type(String train_type) {
        this.train_type = train_type;
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

    public Time getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(Time departure_time) {
        this.departure_time = departure_time;
    }

    public Time getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(Time arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getRun_time() {
        return run_time;
    }

    public void setRun_time(String run_time) {
        this.run_time = run_time;
    }

    public int getTrain_status() {
        return train_status;
    }

    public void setTrain_status(int train_status) {
        this.train_status = train_status;
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

    @Override
    public String toString() {
        return "TrainInfo{" +
                "train_id='" + train_id + '\'' +
                ", train_number='" + train_number + '\'' +
                ", train_type='" + train_type + '\'' +
                ", start_station_id=" + start_station_id +
                ", end_station_id=" + end_station_id +
                ", departure_time=" + departure_time +
                ", arrival_time=" + arrival_time +
                ", run_time='" + run_time + '\'' +
                ", train_status=" + train_status +
                ", start_station_name='" + start_station_name + '\'' +
                ", end_station_name='" + end_station_name + '\'' +
                '}';
    }
}