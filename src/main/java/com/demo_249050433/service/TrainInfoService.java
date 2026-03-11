// TrainInfoService.java
package com.demo_249050433.service;

import com.demo_249050433.entity.SeatInfo;
import com.demo_249050433.entity.StationInfo;
import com.demo_249050433.entity.TicketOrderInfo;
import com.demo_249050433.entity.TrainInfo;
import com.demo_249050433.mapper.TrainInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 高铁票务信息服务层
 * 处理高铁票相关的业务逻辑，包括查询、选座、购票、支付等
 */
@Service
public class TrainInfoService {

    @Autowired
    private TrainInfoMapper trainInfoMapper;

    /**
     * 获取车站列表
     *
     * @return 车站信息列表
     */
    public List<StationInfo> getStationList() {
        List<StationInfo> stationList = null;
        try {
            stationList = trainInfoMapper.getStationList(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationList;
    }

    /**
     * 查询车次列表
     *
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @return 车次信息列表
     */
    public List<TrainInfo> queryTrainList(int startStationId, int endStationId) {
        List<TrainInfo> trainList = null;
        try {
            trainList = trainInfoMapper.queryTrainList(startStationId, endStationId, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainList;
    }

    /**
     * 获取车次详细信息
     *
     * @param trainId 车次ID
     * @return 车次详细信息
     */
    public TrainInfo getTrainDetail(String trainId) {
        TrainInfo trainInfo = null;
        try {
            trainInfo = trainInfoMapper.getTrainDetail(trainId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainInfo;
    }

    /**
     * 获取车厢座位信息（支持可视化选座）
     *
     * @param trainId    车次ID
     * @param carriageId 车厢ID（0表示所有车厢）
     * @return 座位信息列表（按车厢、排、列排序）
     */
    public List<SeatInfo> getSeatList(String trainId, int carriageId) {
        List<SeatInfo> seatList = null;
        try {
            seatList = trainInfoMapper.getSeatList(trainId, carriageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seatList;
    }

    /**
     * 获取车厢座位矩阵（用于前端可视化展示）
     *
     * @param trainId    车次ID
     * @param carriageId 车厢ID
     * @return 座位矩阵数据
     */
    public Map<String, Object> getSeatMatrix(String trainId, int carriageId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<SeatInfo> seatList = trainInfoMapper.getSeatList(trainId, carriageId);

            // 组织成矩阵形式
            Map<String, List<SeatInfo>> seatMatrix = new HashMap<>();
            Map<Integer, List<SeatInfo>> rowSeats = new TreeMap<>();

            for (SeatInfo seat : seatList) {
                int row = seat.getSeat_row();
                if (!rowSeats.containsKey(row)) {
                    rowSeats.put(row, new ArrayList<>());
                }
                rowSeats.get(row).add(seat);
            }

            // 转换为按排排序的列表
            List<List<SeatInfo>> matrixList = new ArrayList<>();
            for (int row = 1; row <= rowSeats.size(); row++) {
                if (rowSeats.containsKey(row)) {
                    matrixList.add(rowSeats.get(row));
                }
            }

            result.put("seat_matrix", matrixList);
            result.put("total_rows", rowSeats.size());
            result.put("total_seats", seatList.size());
            result.put("available_seats", seatList.stream().filter(s -> s.getSeat_status() == 0).count());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 锁定座位（选座）
     *
     * @param seatId 座位ID
     * @return 锁定结果（1:成功，0:失败，2:座位已被占用）
     */
    @Transactional
    public int lockSeat(int seatId) {
        int result = 0;
        try {
            // 检查座位状态
            Integer seatStatus = trainInfoMapper.checkSeatStatus(seatId);
            if (seatStatus == null) {
                return 0; // 座位不存在
            }
            if (seatStatus != 0) {
                return 2; // 座位已被占用或锁定
            }

            // 锁定座位
            result = trainInfoMapper.lockSeat(seatId, new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建订单
     *
     * @param orderInfo 订单信息
     * @return 创建结果（1:成功，0:失败）
     */
    @Transactional
    public int createOrder(TicketOrderInfo orderInfo) {
        int result = 0;
        try {
            // 生成订单号
            String orderNo = "TICKET" + System.currentTimeMillis() + (int) (Math.random() * 1000);
            orderInfo.setOrder_no(orderNo);

            // 创建订单
            result = trainInfoMapper.createOrder(orderInfo);

            if (result == 1) {
                // 标记座位为已售
                trainInfoMapper.sellSeat(orderInfo.getSeat_id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 支付结果（1:成功，0:失败，2:订单不存在，3:订单已支付）
     */
    @Transactional
    public int payOrder(int orderId) {
        int result = 0;
        try {
            // 获取订单详情
            TicketOrderInfo orderInfo = trainInfoMapper.getOrderDetail(orderId);
            if (orderInfo == null) {
                return 2; // 订单不存在
            }
            if (orderInfo.getOrder_status() == 1) {
                return 3; // 订单已支付
            }

            // 模拟支付流程
            // 在实际项目中，这里会调用第三方支付接口

            // 更新订单状态为已支付
            result = trainInfoMapper.updateOrderStatus(orderId, 1, "已支付");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 取消结果（1:成功，0:失败，2:订单不存在）
     */
    @Transactional
    public int cancelOrder(int orderId) {
        int result = 0;
        try {
            // 获取订单详情
            TicketOrderInfo orderInfo = trainInfoMapper.getOrderDetail(orderId);
            if (orderInfo == null) {
                return 2; // 订单不存在
            }

            // 如果订单未支付，释放座位
            if (orderInfo.getOrder_status() == 0) {
                trainInfoMapper.releaseSeat(orderInfo.getSeat_id());
            }

            // 更新订单状态为已取消
            result = trainInfoMapper.updateOrderStatus(orderId, 2, "已取消");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详细信息
     */
    public TicketOrderInfo getOrderDetail(int orderId) {
        TicketOrderInfo orderInfo = null;
        try {
            orderInfo = trainInfoMapper.getOrderDetail(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderInfo;
    }

    /**
     * 获取用户订单列表
     *
     * @param stuNo       学号
     * @param orderStatus 订单状态（0表示所有状态）
     * @return 订单列表
     */
    public List<TicketOrderInfo> getOrderList(int stuNo, int orderStatus) {
        List<TicketOrderInfo> orderList = null;
        try {
            orderList = trainInfoMapper.getOrderList(stuNo, orderStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderList;
    }

    /**
     * 获取车票价格
     *
     * @param trainId        车次ID
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @param carriageType   车厢类型
     * @return 票价
     */
    public Float getTicketPrice(String trainId, int startStationId, int endStationId, String carriageType) {
        Float price = null;
        try {
            price = trainInfoMapper.getTicketPrice(trainId, startStationId, endStationId, carriageType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }
    // TrainInfoService.java - 添加以下方法

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单详细信息
     */
    public TicketOrderInfo getOrderDetailByNo(String orderNo) {
        TicketOrderInfo orderInfo = null;
        try {
            // 这里需要先实现这个方法，如果Mapper中还没有这个方法，需要先添加
            orderInfo = trainInfoMapper.getOrderDetailByNo(orderNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderInfo;
    }
}