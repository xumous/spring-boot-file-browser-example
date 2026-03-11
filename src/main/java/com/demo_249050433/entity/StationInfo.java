// StationInfo.java
package com.demo_249050433.entity;

/**
 * 车站信息实体类
 * 用于映射数据库中的车站信息表记录，将数据库查询结果自动转换为Java对象
 */
public class StationInfo {
    private int station_id;
    private String station_name;
    private String station_code;
    private String city_name;
    private String station_type;
    private int station_status;

    // Getter和Setter方法
    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getStation_code() {
        return station_code;
    }

    public void setStation_code(String station_code) {
        this.station_code = station_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }

    public int getStation_status() {
        return station_status;
    }

    public void setStation_status(int station_status) {
        this.station_status = station_status;
    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "station_id=" + station_id +
                ", station_name='" + station_name + '\'' +
                ", station_code='" + station_code + '\'' +
                ", city_name='" + city_name + '\'' +
                ", station_type='" + station_type + '\'' +
                ", station_status=" + station_status +
                '}';
    }
}