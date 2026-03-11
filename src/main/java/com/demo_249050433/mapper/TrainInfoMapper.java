// TrainInfoMapper.java
package com.demo_249050433.mapper;

import com.demo_249050433.entity.SeatInfo;
import com.demo_249050433.entity.StationInfo;
import com.demo_249050433.entity.TicketOrderInfo;
import com.demo_249050433.entity.TrainInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 高铁车次信息数据访问层接口
 * 提供与高铁票相关的数据库交互操作
 */
@Mapper
@Repository
public interface TrainInfoMapper {

    /**
     * 查询车站列表
     *
     * @param stationStatus 车站状态
     * @return 车站信息列表
     */
    @Select("SELECT * FROM station_info WHERE station_status = #{stationStatus}")
    List<StationInfo> getStationList(@Param("stationStatus") int stationStatus);

    /**
     * 根据出发站和到达站查询车次
     *
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @param departureDate  出发日期
     * @param trainStatus    车次状态
     * @return 车次信息列表
     */
    @Select("SELECT ti.*, " +
            "       s1.station_name as start_station_name, " +
            "       s2.station_name as end_station_name " +
            "FROM train_info ti " +
            "INNER JOIN station_info s1 ON s1.station_id = ti.start_station_id " +
            "INNER JOIN station_info s2 ON s2.station_id = ti.end_station_id " +
            "WHERE (ti.start_station_id = #{startStationId} OR #{startStationId} = 0) " +
            "  AND (ti.end_station_id = #{endStationId} OR #{endStationId} = 0) " +
            "  AND ti.train_status = #{trainStatus} " +
            "ORDER BY ti.departure_time")
    List<TrainInfo> queryTrainList(@Param("startStationId") int startStationId,
                                   @Param("endStationId") int endStationId,
                                   @Param("trainStatus") int trainStatus);

    /**
     * 查询车次详细信息
     *
     * @param trainId 车次ID
     * @return 车次详细信息
     */
    @Select("SELECT ti.*, " +
            "       s1.station_name as start_station_name, " +
            "       s2.station_name as end_station_name " +
            "FROM train_info ti " +
            "INNER JOIN station_info s1 ON s1.station_id = ti.start_station_id " +
            "INNER JOIN station_info s2 ON s2.station_id = ti.end_station_id " +
            "WHERE ti.train_id = #{trainId} " +
            "  AND ti.train_status = 1")
    TrainInfo getTrainDetail(@Param("trainId") String trainId);

    /**
     * 查询车厢座位信息（支持选座功能）
     *
     * @param trainId    车次ID
     * @param carriageId 车厢ID（0表示查询所有车厢）
     * @return 座位信息列表
     */
    @Select("SELECT si.*, " +
            "       ci.carriage_number, " +
            "       ci.carriage_type " +
            "FROM seat_info si " +
            "INNER JOIN carriage_info ci ON ci.carriage_id = si.carriage_id " +
            "INNER JOIN train_info ti ON ti.train_id = ci.train_id " +
            "WHERE ti.train_id = #{trainId} " +
            "  AND (ci.carriage_id = #{carriageId} OR #{carriageId} = 0) " +
            "ORDER BY ci.carriage_number, si.seat_row, si.seat_col")
    List<SeatInfo> getSeatList(@Param("trainId") String trainId,
                               @Param("carriageId") int carriageId);

    /**
     * 锁定座位（选座功能）
     *
     * @param seatId   座位ID
     * @param lockTime 锁定时间
     * @return 更新影响的行数
     */
    @Update("UPDATE seat_info " +
            "SET seat_status = 2, " +
            "    lock_time = #{lockTime}, " +
            "    seat_status_desc = '已锁定' " +
            "WHERE seat_id = #{seatId} " +
            "  AND seat_status = 0")
    int lockSeat(@Param("seatId") int seatId,
                 @Param("lockTime") Date lockTime);

    /**
     * 释放座位（解锁）
     *
     * @param seatId 座位ID
     * @return 更新影响的行数
     */
    @Update("UPDATE seat_info " +
            "SET seat_status = 0, " +
            "    lock_time = NULL, " +
            "    seat_status_desc = '可售' " +
            "WHERE seat_id = #{seatId} " +
            "  AND seat_status = 2")
    int releaseSeat(@Param("seatId") int seatId);

    /**
     * 创建订单
     *
     * @param orderInfo 订单信息对象
     * @return 插入影响的行数
     */
    @Insert("INSERT INTO ticket_order_info " +
            "(order_no, stu_no, train_id, start_station_id, end_station_id, " +
            " carriage_id, seat_id, passenger_name, passenger_id_card, " +
            " ticket_price, order_status, order_time, order_expire, order_status_desc) " +
            "VALUES (#{order_no}, #{stu_no}, #{train_id}, #{start_station_id}, #{end_station_id}, " +
            "        #{carriage_id}, #{seat_id}, #{passenger_name}, #{passenger_id_card}, " +
            "        #{ticket_price}, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), '待支付')")
    @Options(useGeneratedKeys = true, keyProperty = "order_id")
    int createOrder(TicketOrderInfo orderInfo);

    /**
     * 更新座位状态为已售
     *
     * @param seatId 座位ID
     * @return 更新影响的行数
     */
    @Update("UPDATE seat_info " +
            "SET seat_status = 1, " +
            "    lock_time = NULL, " +
            "    seat_status_desc = '已售' " +
            "WHERE seat_id = #{seatId}")
    int sellSeat(@Param("seatId") int seatId);

    /**
     * 更新订单支付状态
     *
     * @param orderId     订单ID
     * @param orderStatus 订单状态
     * @param statusDesc  状态描述
     * @return 更新影响的行数
     */
    @Update("UPDATE ticket_order_info " +
            "SET order_status = #{orderStatus}, " +
            "    pay_time = NOW(), " +
            "    order_status_desc = #{statusDesc} " +
            "WHERE order_id = #{orderId}")
    int updateOrderStatus(@Param("orderId") int orderId,
                          @Param("orderStatus") int orderStatus,
                          @Param("statusDesc") String statusDesc);

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单详细信息
     */
    @Select("SELECT o.*, " +
            "       ti.train_number, " +
            "       s1.station_name as start_station_name, " +
            "       s2.station_name as end_station_name, " +
            "       si.seat_number, " +
            "       ci.carriage_number " +
            "FROM ticket_order_info o " +
            "INNER JOIN train_info ti ON ti.train_id = o.train_id " +
            "INNER JOIN station_info s1 ON s1.station_id = o.start_station_id " +
            "INNER JOIN station_info s2 ON s2.station_id = o.end_station_id " +
            "INNER JOIN seat_info si ON si.seat_id = o.seat_id " +
            "INNER JOIN carriage_info ci ON ci.carriage_id = o.carriage_id " +
            "WHERE o.order_id = #{orderId}")
    TicketOrderInfo getOrderDetail(@Param("orderId") int orderId);

    /**
     * 查询用户订单列表
     *
     * @param stuNo       学号（用户ID）
     * @param orderStatus 订单状态（0表示查询所有状态）
     * @return 订单列表
     */
    @Select("SELECT o.*, " +
            "       ti.train_number, " +
            "       s1.station_name as start_station_name, " +
            "       s2.station_name as end_station_name, " +
            "       si.seat_number, " +
            "       ci.carriage_number " +
            "FROM ticket_order_info o " +
            "INNER JOIN train_info ti ON ti.train_id = o.train_id " +
            "INNER JOIN station_info s1 ON s1.station_id = o.start_station_id " +
            "INNER JOIN station_info s2 ON s2.station_id = o.end_station_id " +
            "INNER JOIN seat_info si ON si.seat_id = o.seat_id " +
            "INNER JOIN carriage_info ci ON ci.carriage_id = o.carriage_id " +
            "WHERE o.stu_no = #{stuNo} " +
            "  AND (o.order_status = #{orderStatus} OR #{orderStatus} = 0) " +
            "ORDER BY o.order_time DESC")
    List<TicketOrderInfo> getOrderList(@Param("stuNo") int stuNo,
                                       @Param("orderStatus") int orderStatus);

    /**
     * 查询车票价格
     *
     * @param trainId        车次ID
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @param carriageType   车厢类型
     * @return 票价
     */
    @Select("SELECT ticket_price " +
            "FROM ticket_price_info " +
            "WHERE train_id = #{trainId} " +
            "  AND start_station_id = #{startStationId} " +
            "  AND end_station_id = #{endStationId} " +
            "  AND carriage_type = #{carriageType} " +
            "  AND price_status = 1")
    Float getTicketPrice(@Param("trainId") String trainId,
                         @Param("startStationId") int startStationId,
                         @Param("endStationId") int endStationId,
                         @Param("carriageType") String carriageType);

    /**
     * 检查座位是否可售
     *
     * @param seatId 座位ID
     * @return 座位状态（0表示可售）
     */
    @Select("SELECT seat_status FROM seat_info WHERE seat_id = #{seatId}")
    Integer checkSeatStatus(@Param("seatId") int seatId);
// TrainInfoMapper.java - 添加以下方法

    /**
     * 根据订单号查询订单详情
     *
     * @param orderNo 订单号
     * @return 订单详细信息
     */
    @Select("SELECT o.*, " +
            "       ti.train_number, " +
            "       s1.station_name as start_station_name, " +
            "       s2.station_name as end_station_name, " +
            "       si.seat_number, " +
            "       ci.carriage_number " +
            "FROM ticket_order_info o " +
            "INNER JOIN train_info ti ON ti.train_id = o.train_id " +
            "INNER JOIN station_info s1 ON s1.station_id = o.start_station_id " +
            "INNER JOIN station_info s2 ON s2.station_id = o.end_station_id " +
            "INNER JOIN seat_info si ON si.seat_id = o.seat_id " +
            "INNER JOIN carriage_info ci ON ci.carriage_id = o.carriage_id " +
            "WHERE o.order_no = #{orderNo}")
    TicketOrderInfo getOrderDetailByNo(@Param("orderNo") String orderNo);
}