// pages/login/login.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        stu_no: "",
        stu_pwd: "",
        loading: false,
        rememberMe: false,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        // 检查本地存储中是否有记住的账号
        const rememberMe = wx.getStorageSync("rememberMe");
        const savedStuNo = wx.getStorageSync("savedStuNo");

        if (rememberMe && savedStuNo) {
            this.setData({
                rememberMe: true,
                stu_no: savedStuNo,
            });
        }
    },

    /**
     * 输入学号
     */
    inputStuNo: function (e) {
        this.setData({
            stu_no: e.detail.value,
        });
    },

    /**
     * 输入密码
     */
    inputStuPwd: function (e) {
        this.setData({
            stu_pwd: e.detail.value,
        });
    },

    /**
     * 切换记住我选项
     */
    toggleRememberMe: function () {
        this.setData({
            rememberMe: !this.data.rememberMe,
        });
    },

    /**
     * 用户登录
     */
    login: function () {
        const that = this;
        const stu_no = this.data.stu_no.trim();
        const stu_pwd = this.data.stu_pwd.trim();

        // 表单验证
        if (!stu_no) {
            wx.showToast({
                title: "请输入学号",
                icon: "none",
            });
            return;
        }

        if (!stu_pwd) {
            wx.showToast({
                title: "请输入密码",
                icon: "none",
            });
            return;
        }

        // 验证学号格式（纯数字）
        if (!/^\d+$/.test(stu_no)) {
            wx.showToast({
                title: "学号应为数字",
                icon: "none",
            });
            return;
        }

        this.setData({
            loading: true,
        });

        wx.request({
            url: "http://localhost:8080/login/h5_login",
            method: "POST",
            header: {
                "content-type": "application/json",
            },
            data: {
                stu_no: stu_no,
                stu_pwd: stu_pwd,
            },
            success: function (res) {
                console.log("登录返回:", res.data);
                that.setData({
                    loading: false,
                });

                if (res.data.code === 200) {
                    // 登录成功
                    const token = res.data.token;
                    const userInfo = res.data.userInfo;

                    // 保存token到本地存储
                    wx.setStorageSync("token", token);
                    wx.setStorageSync("userInfo", userInfo);
                    wx.setStorageSync("stu_no", userInfo.stu_no);

                    // 如果勾选了记住我，保存学号
                    if (that.data.rememberMe) {
                        wx.setStorageSync("rememberMe", true);
                        wx.setStorageSync("savedStuNo", stu_no);
                    } else {
                        wx.removeStorageSync("rememberMe");
                        wx.removeStorageSync("savedStuNo");
                    }

                    wx.showToast({
                        title: "登录成功",
                        icon: "success",
                        duration: 1500,
                        success: function () {
                            // 跳转到选车页面
                            setTimeout(function () {
                                wx.redirectTo({
                                    url: "/pages/train_query/train_query",
                                });
                            }, 1500);
                        },
                    });
                } else {
                    wx.showToast({
                        title: res.data.message || "登录失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                console.error("登录请求失败:", error);
                that.setData({
                    loading: false,
                });
                wx.showToast({
                    title: "网络错误，请检查网络连接",
                    icon: "none",
                });
            },
        });
    },

    /**
     * 跳转到注册页面
     */
    goToRegister: function () {
        wx.navigateTo({
            url: "/pages/register/register",
        });
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        // 检查是否已登录
        const token = wx.getStorageSync("token");
        if (token) {
            // 验证token是否有效
            wx.request({
                url: "http://localhost:8080/login/h5_validate_token",
                method: "POST",
                header: {
                    "content-type": "application/json",
                },
                data: {token: token},
                success: function (res) {
                    if (res.data.code === 200) {
                        // token有效，直接跳转到选车页面
                        wx.redirectTo({
                            url: "/pages/train_query/train_query",
                        });
                    }
                },
            });
        }
    },
});