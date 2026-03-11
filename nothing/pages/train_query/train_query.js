// pages/train_query/train_query.js
Page({
    /**
     * 页面的初始数据
     * @property {Array} stationList - 车站列表
     * @property {Object} queryParams - 查询参数
     * @property {number} queryParams.startStationId - 出发站ID
     * @property {number} queryParams.endStationId - 到达站ID
     * @property {Array} trainList - 车次列表
     * @property {Object} queryResult - 查询结果
     * @property {boolean} showStationModal - 是否显示车站选择模态框
     * @property {string} stationModalType - 车站选择类型（start/end）
     */
    data: {
        stationList: [],
        queryParams: {
            startStationId: 0,
            endStationId: 0,
            startStationName: "请选择",
            endStationName: "请选择",
        },
        trainList: [],
        queryResult: {
            code: 0,
            message: "",
            data: [],
        },
        showStationModal: false,
        stationModalType: "start",
    },

    /**
     * 获取车站列表
     */
    getStationList() {
        var that = this;
        wx.request({
            url: "http://localhost:8080/h5_get_station_list",
            success: function (res) {
                console.log("车站列表:", res.data);
                if (res.data.code === 200) {
                    that.setData({
                        stationList: res.data.data,
                    });
                }
            },
            fail: function (error) {
                console.error("获取车站列表失败:", error);
                wx.showToast({
                    title: "获取车站列表失败",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 显示出发站选择模态框
     */
    showStartStationModal: function () {
        this.setData({
            showStationModal: true,
            stationModalType: "start",
        });
    },

    /**
     * 显示到达站选择模态框
     */
    showEndStationModal: function () {
        this.setData({
            showStationModal: true,
            stationModalType: "end",
        });
    },

    /**
     * 关闭车站选择模态框
     */
    closeStationModal: function () {
        this.setData({
            showStationModal: false,
        });
    },

    /**
     * 选择出发站
     */
    chooseStartStation: function (e) {
        var stationId = e.currentTarget.dataset.id;
        var stationName = e.currentTarget.dataset.name;
        var queryParams = this.data.queryParams;
        queryParams.startStationId = stationId;
        queryParams.startStationName = stationName;
        this.setData({
            queryParams: queryParams,
        });
        this.closeStationModal();
    },

    /**
     * 选择到达站
     */
    chooseEndStation: function (e) {
        var stationId = e.currentTarget.dataset.id;
        var stationName = e.currentTarget.dataset.name;
        var queryParams = this.data.queryParams;
        queryParams.endStationId = stationId;
        queryParams.endStationName = stationName;
        this.setData({
            queryParams: queryParams,
        });
        this.closeStationModal();
    },

    /**
     * 交换出发站和到达站
     */
    exchangeStations: function () {
        var queryParams = this.data.queryParams;
        var tempId = queryParams.startStationId;
        var tempName = queryParams.startStationName;

        queryParams.startStationId = queryParams.endStationId;
        queryParams.startStationName = queryParams.endStationName;
        queryParams.endStationId = tempId;
        queryParams.endStationName = tempName;

        this.setData({
            queryParams: queryParams,
        });
    },

    /**
     * 查询车次
     */
    queryTrains: function () {
        var that = this;
        var queryParams = this.data.queryParams;

        if (queryParams.startStationId === 0 || queryParams.endStationId === 0) {
            wx.showToast({
                title: "请选择车站",
                icon: "none",
            });
            return;
        }

        if (queryParams.startStationId === queryParams.endStationId) {
            wx.showToast({
                title: "出发站和到达站不能相同",
                icon: "none",
            });
            return;
        }

        wx.showLoading({
            title: "查询中...",
        });

        wx.request({
            url: "http://localhost:8080/h5_query_train_list",
            data: {
                startStationId: queryParams.startStationId,
                endStationId: queryParams.endStationId,
            },
            success: function (res) {
                wx.hideLoading();
                console.log("车次列表:", res.data);
                that.setData({
                    queryResult: res.data,
                    trainList: res.data.data || [],
                });
            },
            fail: function () {
                wx.hideLoading();
                wx.showToast({
                    title: "查询失败",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 跳转到车次详情页面
     */
    goToTrainDetail: function (e) {
        var trainId = e.currentTarget.dataset.id;
        console.log("查看车次详情:", trainId);
        // 这里可以跳转到车次详情页面
        // wx.navigateTo({
        //     url: '/pages/train_detail/train_detail?trainId=' + trainId
        // });
    },

    /**
     * 跳转到座位选择页面（预订）
     */
    goToSeatSelection: function (e) {
        var trainId = e.currentTarget.dataset.id;
        console.log("预订车次:", trainId);

        // 跳转到座位选择页面
        wx.navigateTo({
            url: "/pages/seat_selection/seat_selection?trainId=" + trainId,
        });
    },

    // 在train_query.js的onLoad函数前添加以下代码2025年12月5日10点26分：
    /**
     * 检查登录状态
     */
    // 在train_query.js中修改checkLoginStatus函数2025年12月5日10点48分：
    checkLoginStatus: function () {
        const token = wx.getStorageSync("token");
        const userInfo = wx.getStorageSync("userInfo");

        if (!token || !userInfo) {
            // 未登录，跳转到登录页面
            wx.redirectTo({
                url: "/pages/login/login",
            });
            return false;
        }

        // 验证token是否有效
        wx.request({
            url: "http://localhost:8080/login/h5_validate_token",
            method: "POST",
            header: {
                "content-type": "application/json",
            },
            data: {token: token},
            success: (res) => {
                if (res.data.code !== 200) {
                    // token无效，清除本地存储并跳转到登录页面
                    wx.removeStorageSync("token");
                    wx.removeStorageSync("userInfo");
                    wx.removeStorageSync("stu_no");
                    wx.redirectTo({
                        url: "/pages/login/login",
                    });
                } else {
                    // 显示用户信息
                    this.setData({
                        userInfo: userInfo,
                    });
                }
            },
            fail: () => {
                // 网络错误，保持当前状态
            },
        });

        return true;
    },

    /**
     * 用户注销
     */
    logout: function () {
        const token = wx.getStorageSync("token");

        if (token) {
            wx.request({
                url: "http://localhost:8080/login/h5_logout",
                method: "POST",
                header: {
                    "content-type": "application/json",
                },
                data: {token: token},
                success: () => {
                    // 清除本地存储
                    wx.removeStorageSync("token");
                    wx.removeStorageSync("userInfo");
                    wx.removeStorageSync("stu_no");

                    // 跳转到登录页面
                    wx.redirectTo({
                        url: "/pages/login/login",
                    });
                },
            });
        }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    // 修改onLoad函数2025年12月5日10点26分：
    onLoad: function (options) {
        // 检查登录状态
        if (this.checkLoginStatus()) {
            this.getStationList();
        }
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
        this.queryTrains();
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