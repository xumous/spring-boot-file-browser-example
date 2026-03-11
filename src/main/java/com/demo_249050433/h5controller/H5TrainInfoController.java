// H5TrainInfoController.java
package com.demo_249050433.h5controller;

import com.demo_249050433.entity.SeatInfo;
import com.demo_249050433.entity.StationInfo;
import com.demo_249050433.entity.TicketOrderInfo;
import com.demo_249050433.entity.TrainInfo;
import com.demo_249050433.service.TrainInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5高铁票务控制器
 * 为微信小程序前端提供RESTful API接口，返回JSON格式数据
 */
@RestController
public class H5TrainInfoController {

    @Autowired
    private TrainInfoService trainInfoService;

    /**
     * 获取车站列表
     *
     * @return 车站信息列表（JSON格式）
     */
    @RequestMapping("h5_get_station_list")
    public Map<String, Object> h5GetStationList() {
        System.out.println("获取车站列表");

        Map<String, Object> result = new HashMap<>();
        try {
            List<StationInfo> stationList = trainInfoService.getStationList();

            if (stationList != null && !stationList.isEmpty()) {
                result.put("code", 200);
                result.put("message", "获取车站列表成功");
                result.put("data", stationList);
                System.out.println("车站数量: " + stationList.size());
            } else {
                result.put("code", 404);
                result.put("message", "未找到车站信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 查询车次列表
     *
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @return 车次信息列表（JSON格式）
     */
    @RequestMapping("h5_query_train_list")
    public Map<String, Object> h5QueryTrainList(
            @RequestParam("startStationId") int startStationId,
            @RequestParam("endStationId") int endStationId) {
        System.out.println("查询车次列表 - startStationId:" + startStationId + ", endStationId:" + endStationId);

        Map<String, Object> result = new HashMap<>();
        try {
            List<TrainInfo> trainList = trainInfoService.queryTrainList(startStationId, endStationId);

            if (trainList != null && !trainList.isEmpty()) {
                result.put("code", 200);
                result.put("message", "查询车次列表成功");
                result.put("data", trainList);
                System.out.println("车次数量: " + trainList.size());
            } else {
                result.put("code", 404);
                result.put("message", "未找到符合条件的车次");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取车次详细信息
     *
     * @param trainId 车次ID
     * @return 车次详细信息（JSON格式）
     */
    @RequestMapping("h5_get_train_detail")
    public Map<String, Object> h5GetTrainDetail(@RequestParam("trainId") String trainId) {
        System.out.println("获取车次详情 - trainId:" + trainId);

        Map<String, Object> result = new HashMap<>();
        try {
            TrainInfo trainInfo = trainInfoService.getTrainDetail(trainId);

            if (trainInfo != null) {
                result.put("code", 200);
                result.put("message", "获取车次详情成功");
                result.put("data", trainInfo);
                System.out.println("车次信息: " + trainInfo.toString());
            } else {
                result.put("code", 404);
                result.put("message", "未找到车次信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取车厢座位信息（支持可视化选座）
     *
     * @param trainId    车次ID
     * @param carriageId 车厢ID（0表示所有车厢）
     * @return 座位信息列表（JSON格式）
     */
    @RequestMapping("h5_get_seat_list")
    public Map<String, Object> h5GetSeatList(
            @RequestParam("trainId") String trainId,
            @RequestParam(value = "carriageId", defaultValue = "0") int carriageId) {
        System.out.println("获取座位列表 - trainId:" + trainId + ", carriageId:" + carriageId);

        Map<String, Object> result = new HashMap<>();
        try {
            List<SeatInfo> seatList = trainInfoService.getSeatList(trainId, carriageId);

            if (seatList != null && !seatList.isEmpty()) {
                result.put("code", 200);
                result.put("message", "获取座位列表成功");
                result.put("data", seatList);
                System.out.println("座位数量: " + seatList.size());
            } else {
                result.put("code", 404);
                result.put("message", "未找到座位信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取车厢座位矩阵（用于前端可视化展示）
     *
     * @param trainId    车次ID
     * @param carriageId 车厢ID
     * @return 座位矩阵数据（JSON格式）
     */
    @RequestMapping("h5_get_seat_matrix")
    public Map<String, Object> h5GetSeatMatrix(
            @RequestParam("trainId") String trainId,
            @RequestParam("carriageId") int carriageId) {
        System.out.println("获取座位矩阵 - trainId:" + trainId + ", carriageId:" + carriageId);

        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> seatMatrix = trainInfoService.getSeatMatrix(trainId, carriageId);

            if (seatMatrix != null && !seatMatrix.isEmpty()) {
                result.put("code", 200);
                result.put("message", "获取座位矩阵成功");
                result.put("data", seatMatrix);
                System.out.println("座位矩阵数据已返回");
            } else {
                result.put("code", 404);
                result.put("message", "未找到座位矩阵信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 锁定座位（选座）
     *
     * @param seatId 座位ID
     * @return 锁定结果（JSON格式）
     */
    @RequestMapping("h5_lock_seat")
    public Map<String, Object> h5LockSeat(@RequestParam("seatId") int seatId) {
        System.out.println("锁定座位 - seatId:" + seatId);

        Map<String, Object> result = new HashMap<>();
        try {
            int lockResult = trainInfoService.lockSeat(seatId);

            if (lockResult == 1) {
                result.put("code", 200);
                result.put("message", "座位锁定成功");
                result.put("data", seatId);
            } else if (lockResult == 2) {
                result.put("code", 400);
                result.put("message", "座位已被占用");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "座位锁定失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 创建订单
     *
     * @param stuNo           学号（用户ID）
     * @param trainId         车次ID
     * @param startStationId  出发站ID
     * @param endStationId    到达站ID
     * @param carriageId      车厢ID
     * @param seatId          座位ID
     * @param passengerName   乘客姓名
     * @param passengerIdCard 乘客身份证
     * @param ticketPrice     票价
     * @return 创建结果（JSON格式）
     */
    @RequestMapping("h5_create_order")
    public Map<String, Object> h5CreateOrder(
            @RequestParam("stuNo") int stuNo,
            @RequestParam("trainId") String trainId,
            @RequestParam("startStationId") int startStationId,
            @RequestParam("endStationId") int endStationId,
            @RequestParam("carriageId") int carriageId,
            @RequestParam("seatId") int seatId,
            @RequestParam("passengerName") String passengerName,
            @RequestParam("passengerIdCard") String passengerIdCard,
            @RequestParam("ticketPrice") float ticketPrice) {
        System.out.println("创建订单 - stuNo:" + stuNo + ", trainId:" + trainId + ", seatId:" + seatId);

        Map<String, Object> result = new HashMap<>();
        try {
            TicketOrderInfo orderInfo = new TicketOrderInfo();
            orderInfo.setStu_no(stuNo);
            orderInfo.setTrain_id(trainId);
            orderInfo.setStart_station_id(startStationId);
            orderInfo.setEnd_station_id(endStationId);
            orderInfo.setCarriage_id(carriageId);
            orderInfo.setSeat_id(seatId);
            orderInfo.setPassenger_name(passengerName);
            orderInfo.setPassenger_id_card(passengerIdCard);
            orderInfo.setTicket_price(new BigDecimal(ticketPrice));

            int createResult = trainInfoService.createOrder(orderInfo);

            if (createResult == 1) {
                result.put("code", 200);
                result.put("message", "订单创建成功");
                result.put("data", orderInfo.getOrder_no());
                System.out.println("订单创建成功，订单号: " + orderInfo.getOrder_no());
            } else {
                result.put("code", 400);
                result.put("message", "订单创建失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 支付结果（JSON格式）
     */
    @RequestMapping("h5_pay_order")
    public Map<String, Object> h5PayOrder(@RequestParam("orderId") int orderId) {
        System.out.println("支付订单 - orderId:" + orderId);

        Map<String, Object> result = new HashMap<>();
        try {
            int payResult = trainInfoService.payOrder(orderId);

            if (payResult == 1) {
                result.put("code", 200);
                result.put("message", "订单支付成功");
                result.put("data", orderId);
            } else if (payResult == 2) {
                result.put("code", 404);
                result.put("message", "订单不存在");
                result.put("data", null);
            } else if (payResult == 3) {
                result.put("code", 400);
                result.put("message", "订单已支付");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "订单支付失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 取消结果（JSON格式）
     */
    @RequestMapping("h5_cancel_order")
    public Map<String, Object> h5CancelOrder(@RequestParam("orderId") int orderId) {
        System.out.println("取消订单 - orderId:" + orderId);

        Map<String, Object> result = new HashMap<>();
        try {
            int cancelResult = trainInfoService.cancelOrder(orderId);

            if (cancelResult == 1) {
                result.put("code", 200);
                result.put("message", "订单取消成功");
                result.put("data", orderId);
            } else if (cancelResult == 2) {
                result.put("code", 404);
                result.put("message", "订单不存在");
                result.put("data", null);
            } else {
                result.put("code", 400);
                result.put("message", "订单取消失败");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详细信息（JSON格式）
     */
    @RequestMapping("h5_get_order_detail")
    public Map<String, Object> h5GetOrderDetail(@RequestParam("orderId") int orderId) {
        System.out.println("获取订单详情 - orderId:" + orderId);

        Map<String, Object> result = new HashMap<>();
        try {
            TicketOrderInfo orderInfo = trainInfoService.getOrderDetail(orderId);

            if (orderInfo != null) {
                result.put("code", 200);
                result.put("message", "获取订单详情成功");
                result.put("data", orderInfo);
                System.out.println("订单信息: " + orderInfo.toString());
            } else {
                result.put("code", 404);
                result.put("message", "未找到订单信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取用户订单列表
     *
     * @param stuNo       学号
     * @param orderStatus 订单状态（0表示所有状态）
     * @return 订单列表（JSON格式）
     */
    @RequestMapping("h5_get_order_list")
    public Map<String, Object> h5GetOrderList(
            @RequestParam("stuNo") int stuNo,
            @RequestParam(value = "orderStatus", defaultValue = "0") int orderStatus) {
        System.out.println("获取订单列表 - stuNo:" + stuNo + ", orderStatus:" + orderStatus);

        Map<String, Object> result = new HashMap<>();
        try {
            List<TicketOrderInfo> orderList = trainInfoService.getOrderList(stuNo, orderStatus);

            if (orderList != null && !orderList.isEmpty()) {
                result.put("code", 200);
                result.put("message", "获取订单列表成功");
                result.put("data", orderList);
                System.out.println("订单数量: " + orderList.size());
            } else {
                result.put("code", 404);
                result.put("message", "未找到订单信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 获取车票价格
     *
     * @param trainId        车次ID
     * @param startStationId 出发站ID
     * @param endStationId   到达站ID
     * @param carriageType   车厢类型
     * @return 票价信息（JSON格式）
     */
    @RequestMapping("h5_get_ticket_price")
    public Map<String, Object> h5GetTicketPrice(
            @RequestParam("trainId") String trainId,
            @RequestParam("startStationId") int startStationId,
            @RequestParam("endStationId") int endStationId,
            @RequestParam("carriageType") String carriageType) {
        System.out.println("获取票价 - trainId:" + trainId + ", carriageType:" + carriageType);

        Map<String, Object> result = new HashMap<>();
        try {
            Float price = trainInfoService.getTicketPrice(trainId, startStationId, endStationId, carriageType);

            if (price != null) {
                result.put("code", 200);
                result.put("message", "获取票价成功");
                result.put("data", price);
                System.out.println("票价: " + price);
            } else {
                result.put("code", 404);
                result.put("message", "未找到票价信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
    // 在 H5TrainInfoController.java 中添加

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单详细信息（JSON格式）
     */
    @RequestMapping("h5_get_order_by_no")
    public Map<String, Object> h5GetOrderByNo(@RequestParam("orderNo") String orderNo) {
        System.out.println("根据订单号获取订单详情 - orderNo:" + orderNo);

        Map<String, Object> result = new HashMap<>();
        try {
            // 通过Service层调用，而不是直接使用Mapper
            TicketOrderInfo orderInfo = trainInfoService.getOrderDetailByNo(orderNo);

            if (orderInfo != null) {
                result.put("code", 200);
                result.put("message", "获取订单详情成功");
                result.put("data", orderInfo);
                System.out.println("订单信息: " + orderInfo.toString());
            } else {
                result.put("code", 404);
                result.put("message", "未找到订单信息");
                result.put("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器错误: " + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
}