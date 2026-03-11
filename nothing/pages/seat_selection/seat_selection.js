// pages/seat_selection/seat_selection.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        trainInfo: {},
        carriageList: [
            {id: 4, number: "01", type: "商务座", price: 300},
            {id: 5, number: "02", type: "一等座", price: 200},
            {id: 6, number: "03", type: "二等座", price: 100},
        ],
        currentCarriageId: 6, // 默认选择二等座车厢
        seatMatrix: [],
        selectedSeat: null,
        passengerInfo: {
            name: "",
            idCard: "",
        },
        loading: false,
        error: null,
        seatStatusMap: {
            0: "available", // 可售
            1: "sold", // 已售
            2: "locked", // 锁定
        },
        trainId: null, // 添加trainId到data
    },

    /**
     * 获取车次信息
     */
    getTrainInfo: function (trainId) {
        var that = this;
        console.log("获取车次信息，trainId:", trainId);

        wx.request({
            url: "http://localhost:8080/h5_get_train_detail",
            data: {trainId: trainId},
            success: function (res) {
                console.log("车次信息返回:", res.data);
                if (res.data.code === 200) {
                    that.setData({
                        trainInfo: res.data.data,
                    });
                } else {
                    console.error("获取车次信息失败:", res.data.message);
                }
            },
            fail: function (error) {
                console.error("请求车次信息失败:", error);
            },
        });
    },

    /**
     * 获取座位矩阵数据 - 修复版本
     */
    getSeatMatrix: function () {
        var that = this;
        var trainId = this.data.trainId;
        var carriageId = this.data.currentCarriageId;

        console.log("获取座位矩阵，参数:", {trainId, carriageId});

        this.setData({
            loading: true,
            error: null,
            seatMatrix: [], // 清空旧数据
            selectedSeat: null, // 清空已选座位
        });

        wx.request({
            url: "http://localhost:8080/h5_get_seat_matrix",
            data: {
                trainId: trainId,
                carriageId: carriageId,
            },
            success: function (res) {
                console.log("座位矩阵返回原始数据:", res.data);
                that.setData({
                    loading: false,
                });

                if (res.data.code === 200) {
                    // 处理座位数据，确保每个座位有完整信息
                    let seatMatrix = res.data.data.seat_matrix || [];

                    // 添加调试日志，查看数据结构
                    console.log("座位矩阵结构:", JSON.stringify(seatMatrix));
                    if (seatMatrix.length > 0) {
                        console.log("第一行座位数:", seatMatrix[0].length);
                        if (seatMatrix[0].length > 0) {
                            console.log("第一个座位完整数据:", seatMatrix[0][0]);
                            console.log("第一个座位状态字段:", seatMatrix[0][0].seat_status);
                            console.log(
                                "第一个座位状态描述:",
                                seatMatrix[0][0].seat_status_desc
                            );
                        }
                    }

                    // 标准化座位数据
                    seatMatrix = seatMatrix.map((row, rowIndex) => {
                        return row.map((seat, colIndex) => {
                            // 确保座位状态是数字类型
                            let seatStatus = parseInt(seat.seat_status);
                            if (isNaN(seatStatus)) {
                                seatStatus = 0; // 默认可售
                            }

                            // 确保座位有必要的字段
                            return {
                                ...seat,
                                // 确保状态字段是数字
                                seat_status: seatStatus,
                                seat_status_desc:
                                    seat.seat_status_desc ||
                                    (seatStatus === 0
                                        ? "可售"
                                        : seatStatus === 1
                                            ? "已售"
                                            : seatStatus === 2
                                                ? "已锁定"
                                                : "未知"),
                                // 确保座位类型存在
                                seat_type: seat.seat_type || "未知",
                                // 确保座位号存在
                                seat_number:
                                    seat.seat_number ||
                                    `${rowIndex + 1}${String.fromCharCode(65 + colIndex)}`,
                                // 确保座位ID存在
                                seat_id:
                                    seat.seat_id || `${carriageId}_${rowIndex}_${colIndex}`,
                                // 确保车厢ID和车厢号
                                carriage_id: seat.carriage_id || carriageId,
                                carriage_number:
                                    seat.carriage_number ||
                                    that.data.carriageList.find((c) => c.id === carriageId)
                                        ?.number ||
                                    "01",
                                // 确保价格字段
                                seat_price:
                                    seat.seat_price ||
                                    that.data.carriageList.find((c) => c.id === carriageId)
                                        ?.price ||
                                    100,
                            };
                        });
                    });

                    console.log("标准化后的座位矩阵:", seatMatrix);

                    that.setData({
                        seatMatrix: seatMatrix,
                    });
                    console.log("座位矩阵设置成功，行数:", seatMatrix.length);
                } else {
                    that.setData({
                        error: res.data.message || "获取座位数据失败",
                    });
                    wx.showToast({
                        title: res.data.message || "获取座位数据失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                console.error("请求座位矩阵失败:", error);
                that.setData({
                    loading: false,
                    error: "网络请求失败",
                });
                wx.showToast({
                    title: "网络请求失败",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 选择车厢
     */
    selectCarriage: function (e) {
        console.log("选择车厢事件:", e);
        var carriageId = e.currentTarget.dataset.id;
        console.log("选择的车厢ID:", carriageId);

        if (carriageId === this.data.currentCarriageId) {
            return; // 相同车厢不重复加载
        }

        this.setData({
            currentCarriageId: carriageId,
            selectedSeat: null, // 切换车厢时清空已选座位
        });

        // 延迟加载，避免频繁请求
        setTimeout(() => {
            this.getSeatMatrix();
        }, 300);
    },

    /**
     * 选择座位 - 增强调试版本
     */
    selectSeat: function (e) {
        console.log("=== 选择座位开始 ===");
        console.log("事件对象:", e);
        console.log("dataset:", e.currentTarget.dataset);

        // 获取行列索引
        var row = e.currentTarget.dataset.row;
        var col = e.currentTarget.dataset.col;

        console.log("选择的座位位置: row=", row, "col=", col);

        if (row === undefined || col === undefined) {
            console.error("座位位置数据缺失");
            wx.showToast({
                title: "座位数据错误",
                icon: "none",
            });
            return;
        }

        // 从座位矩阵中获取座位数据
        var seatMatrix = this.data.seatMatrix;
        console.log("当前座位矩阵:", seatMatrix);

        if (!seatMatrix || !Array.isArray(seatMatrix) || seatMatrix.length <= row) {
            console.error("座位矩阵数据不完整或格式错误");
            wx.showToast({
                title: "座位数据加载中，请稍后",
                icon: "none",
            });
            return;
        }

        var rowSeats = seatMatrix[row];
        console.log("第", row, "行座位数据:", rowSeats);

        if (!rowSeats || !Array.isArray(rowSeats) || rowSeats.length <= col) {
            console.error("行座位数据不完整");
            return;
        }

        var seat = rowSeats[col];
        console.log("找到的座位对象:", seat);

        if (!seat) {
            console.error("未找到座位数据");
            wx.showToast({
                title: "座位不存在",
                icon: "none",
            });
            return;
        }

        var seatId = seat.seat_id;
        var seatStatus = seat.seat_status;

        console.log("座位ID:", seatId, "状态:", seatStatus);

        // 检查座位状态
        if (seatStatus !== 0) {
            var statusText =
                seatStatus === 1
                    ? "座位已售"
                    : seatStatus === 2
                        ? "座位已锁定"
                        : "座位不可选";
            wx.showToast({
                title: statusText,
                icon: "none",
                duration: 2000,
            });
            return;
        }

        var that = this;

        // 显示加载中
        wx.showLoading({
            title: "锁定座位中...",
            mask: true,
        });

        // 锁定座位
        wx.request({
            url: "http://localhost:8080/h5_lock_seat",
            data: {seatId: seatId},
            success: function (res) {
                wx.hideLoading();
                console.log("锁定座位返回:", res.data);

                if (res.data.code === 200) {
                    // 使用路径更新座位状态
                    var seatPath = `seatMatrix[${row}][${col}]`;

                    // 创建更新对象
                    var updateData = {};
                    updateData[`${seatPath}.seat_status`] = 2;
                    updateData[`${seatPath}.seat_status_desc`] = "已锁定";
                    updateData["selectedSeat"] = {
                        ...seat,
                        seat_status: 2,
                        seat_status_desc: "已锁定",
                    };

                    console.log("更新数据:", updateData);

                    // 更新数据
                    that.setData(updateData, function () {
                        console.log("座位状态更新完成");
                        console.log("更新后的座位:", that.data.seatMatrix[row][col]);
                        console.log("当前选中座位:", that.data.selectedSeat);
                    });

                    wx.showToast({
                        title: "座位锁定成功",
                        icon: "success",
                        duration: 2000,
                    });
                } else {
                    wx.showToast({
                        title: res.data.message || "锁定失败",
                        icon: "none",
                        duration: 2000,
                    });
                }
            },
            fail: function (error) {
                wx.hideLoading();
                console.error("锁定座位失败:", error);
                wx.showToast({
                    title: "网络错误",
                    icon: "none",
                    duration: 2000,
                });
            },
        });
    },

    /**
     * 输入乘客姓名
     */
    inputPassengerName: function (e) {
        var passengerInfo = this.data.passengerInfo;
        passengerInfo.name = e.detail.value;
        this.setData({
            passengerInfo: passengerInfo,
        });
    },

    /**
     * 输入乘客身份证
     */
    inputPassengerIdCard: function (e) {
        var passengerInfo = this.data.passengerInfo;
        passengerInfo.idCard = e.detail.value;
        this.setData({
            passengerInfo: passengerInfo,
        });
    },

    /**
     * 提交订单
     */
    submitOrder: function () {
        console.log("提交订单");
        var that = this;
        var trainInfo = this.data.trainInfo;
        var selectedSeat = this.data.selectedSeat;
        var passengerInfo = this.data.passengerInfo;

        console.log("订单信息:", {trainInfo, selectedSeat, passengerInfo});

        // 验证数据
        if (!selectedSeat) {
            wx.showToast({
                title: "请选择座位",
                icon: "none",
            });
            return;
        }

        if (!passengerInfo.name || passengerInfo.name.trim() === "") {
            wx.showToast({
                title: "请输入乘客姓名",
                icon: "none",
            });
            return;
        }

        if (!passengerInfo.idCard || passengerInfo.idCard.trim() === "") {
            wx.showToast({
                title: "请输入身份证号码",
                icon: "none",
            });
            return;
        }

        // 获取当前用户信息
        var stuNo = 249050433; // 示例学号

        wx.showLoading({
            title: "创建订单中...",
            mask: true,
        });

        wx.request({
            url: "http://localhost:8080/h5_create_order",
            data: {
                stuNo: stuNo,
                trainId: trainInfo.train_id,
                startStationId: trainInfo.start_station_id,
                endStationId: trainInfo.end_station_id,
                carriageId: selectedSeat.carriage_id,
                seatId: selectedSeat.seat_id,
                passengerName: passengerInfo.name,
                passengerIdCard: passengerInfo.idCard,
                ticketPrice: selectedSeat.seat_price || 100,
            },
            success: function (res) {
                wx.hideLoading();
                console.log("创建订单返回:", res.data);
                if (res.data.code === 200) {
                    var orderNo = res.data.data;

                    wx.showModal({
                        title: "订单创建成功",
                        content: "订单号：" + orderNo + "\n请在30分钟内完成支付",
                        showCancel: false,
                        success: function () {
                            // 跳转到订单支付页面，传递订单号
                            wx.navigateTo({
                                url: "/pages/order_payment/order_payment?orderNo=" + orderNo,
                            });
                        },
                    });
                } else {
                    wx.showToast({
                        title: res.data.message || "订单创建失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                wx.hideLoading();
                console.error("创建订单失败:", error);
                wx.showToast({
                    title: "网络错误",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 调试函数：打印当前座位状态
     */
    debugSeatStatus: function () {
        console.log("=== 调试座位状态 ===");
        console.log("当前座位矩阵:", this.data.seatMatrix);
        console.log("当前选中座位:", this.data.selectedSeat);
        console.log("车厢ID:", this.data.currentCarriageId);

        // 打印第一个座位的详细状态
        if (this.data.seatMatrix.length > 0 && this.data.seatMatrix[0].length > 0) {
            const seat = this.data.seatMatrix[0][0];
            console.log("第一个座位详情:", {
                seat_id: seat.seat_id,
                seat_status: seat.seat_status,
                seat_status_desc: seat.seat_status_desc,
                seat_number: seat.seat_number,
                seat_type: seat.seat_type,
            });
        }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log("页面加载，参数:", options);
        var trainId = options.trainId;
        this.setData({
            trainId: trainId,
        });
        this.getTrainInfo(trainId);

        // 延迟获取座位矩阵，确保车次信息先加载
        setTimeout(() => {
            this.getSeatMatrix();
            // 3秒后自动调试
            setTimeout(() => {
                this.debugSeatStatus();
            }, 3000);
        }, 500);
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        console.log("页面初次渲染完成");
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {
    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {
        this.getSeatMatrix();
        wx.stopPullDownRefresh();
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {
    },
});