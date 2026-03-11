// pages/order_payment/order_payment.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        orderInfo: {},
        countdown: 1800, // 30分钟
        paymentMethod: "wechat",
        paying: false,
        orderNo: "", // 新增：存储订单号
        loading: true,
    },

    /**
     * 获取订单信息 - 通过订单号获取订单ID，然后获取订单详情
     */
    getOrderInfo: function (orderNo) {
        var that = this;
        console.log("获取订单信息，订单号:", orderNo);

        this.setData({
            loading: true,
        });

        // 先通过订单号获取订单ID
        // 这里需要调用一个新的后端接口，通过订单号获取订单详情
        // 假设我们直接使用订单号获取订单信息
        wx.request({
            url: "http://localhost:8080/h5_get_order_by_no",
            data: {orderNo: orderNo},
            success: function (res) {
                console.log("订单信息返回:", res.data);
                if (res.data.code === 200) {
                    that.setData({
                        orderInfo: res.data.data,
                        loading: false,
                    });
                } else {
                    // 如果接口不存在，尝试直接使用订单号作为订单ID
                    // 这可能不是最佳实践，但在开发阶段可以使用
                    that.getOrderDetailByNo(orderNo);
                }
            },
            fail: function (error) {
                console.error("获取订单信息失败:", error);
                // 尝试直接使用订单号作为订单ID
                that.getOrderDetailByNo(orderNo);
            },
        });
    },

    /**
     * 直接通过订单号获取订单详情（备用方案）
     */
    getOrderDetailByNo: function (orderNo) {
        var that = this;
        console.log("尝试直接通过订单号获取订单详情:", orderNo);

        // 这里假设后端支持通过订单号获取订单详情
        // 在实际项目中，应该有一个专门的接口
        wx.request({
            url: "http://localhost:8080/h5_get_order_by_no",
            data: {orderNo: orderNo},
            success: function (res) {
                console.log("订单详情返回:", res.data);
                if (res.data.code === 200) {
                    that.setData({
                        orderInfo: res.data.data,
                        loading: false,
                    });
                } else {
                    that.setData({
                        loading: false,
                        error: "获取订单信息失败",
                    });
                    wx.showToast({
                        title: "获取订单信息失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                console.error("获取订单详情失败:", error);
                that.setData({
                    loading: false,
                    error: "网络请求失败",
                });
                wx.showToast({
                    title: "网络错误",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 选择支付方式
     */
    selectPaymentMethod: function (e) {
        var method = e.currentTarget.dataset.method;
        console.log("选择支付方式:", method);
        this.setData({
            paymentMethod: method,
        });
    },

    /**
     * 确认支付
     */
    confirmPayment: function () {
        var that = this;
        var orderInfo = this.data.orderInfo;

        if (!orderInfo || !orderInfo.order_id) {
            wx.showToast({
                title: "订单信息不完整",
                icon: "none",
            });
            return;
        }

        wx.showModal({
            title: "确认支付",
            content: "确认支付 ¥" + (orderInfo.ticket_price || 0) + " 吗？",
            success: function (res) {
                if (res.confirm) {
                    that.processPayment();
                }
            },
        });
    },

    // 修改 processPayment 函数中的支付成功回调部分
    processPayment: function () {
        var that = this;
        var orderInfo = this.data.orderInfo;

        if (!orderInfo || !orderInfo.order_id) {
            wx.showToast({
                title: "订单信息不完整",
                icon: "none",
            });
            return;
        }

        this.setData({
            paying: true,
        });

        wx.request({
            url: "http://localhost:8080/h5_pay_order",
            data: {orderId: orderInfo.order_id},
            success: function (res) {
                console.log("支付返回:", res.data);
                that.setData({
                    paying: false,
                });

                if (res.data.code === 200) {
                    wx.showModal({
                        title: "支付成功",
                        content: "订单支付成功！\n订单号：" + orderInfo.order_no,
                        showCancel: false,
                        success: function () {
                            // 修改这里：跳转回 train_query 页面
                            wx.redirectTo({
                                url: "/pages/train_query/train_query",
                            });

                            // 或者使用以下方式返回到之前的页面
                            // wx.navigateBack({
                            //     delta: 2  // 返回到前两页（假设是从train_query跳转到seat_selection再跳转到order_payment）
                            // });
                        },
                    });
                } else {
                    wx.showToast({
                        title: res.data.message || "支付失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                that.setData({
                    paying: false,
                });
                wx.showToast({
                    title: "网络错误",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 取消订单后的跳转
     */
    cancelOrder: function () {
        var that = this;
        var orderInfo = this.data.orderInfo;

        if (!orderInfo || !orderInfo.order_id) {
            wx.showToast({
                title: "订单信息不完整",
                icon: "none",
            });
            return;
        }

        wx.showModal({
            title: "确认取消",
            content: "确定要取消订单吗？",
            success: function (res) {
                if (res.confirm) {
                    wx.request({
                        url: "http://localhost:8080/h5_cancel_order",
                        data: {orderId: orderInfo.order_id},
                        success: function (res) {
                            if (res.data.code === 200) {
                                wx.showToast({
                                    title: "订单已取消",
                                    icon: "success",
                                });
                                // 修改这里：取消订单后跳转回 seat_selection
                                wx.redirectTo({
                                    url: "/pages/train_query/seat_selection",
                                });
                            } else {
                                wx.showToast({
                                    title: res.data.message || "取消失败",
                                    icon: "none",
                                });
                            }
                        },
                        fail: function (error) {
                            wx.showToast({
                                title: "网络错误",
                                icon: "none",
                            });
                        },
                    });
                }
            },
        });
    },

    /**
     * 倒计时
     */
    startCountdown: function () {
        var that = this;
        var timer = setInterval(function () {
            var countdown = that.data.countdown;
            if (countdown > 0) {
                countdown--;
                that.setData({
                    countdown: countdown,
                });
            } else {
                clearInterval(timer);
                // 订单超时，自动取消
                that.cancelOrder();
            }
        }, 1000);
    },

    /**
     * 格式化时间
     */
    formatTime: function (seconds) {
        var minutes = Math.floor(seconds / 60);
        var secs = seconds % 60;
        return (
            (minutes < 10 ? "0" : "") + minutes + ":" + (secs < 10 ? "0" : "") + secs
        );
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log("页面加载，参数:", options);
        var orderNo = options.orderNo;

        if (!orderNo) {
            wx.showToast({
                title: "订单号不能为空",
                icon: "none",
            });
            wx.navigateBack();
            return;
        }

        this.setData({
            orderNo: orderNo,
        });

        this.getOrderInfo(orderNo);
        this.startCountdown();
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
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