// pages/register/register.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        stu_no: "",
        stu_pwd: "",
        stu_pwd_confirm: "",
        stu_name: "",
        stu_telephone: "",
        loading: false,
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
     * 确认密码
     */
    inputStuPwdConfirm: function (e) {
        this.setData({
            stu_pwd_confirm: e.detail.value,
        });
    },

    /**
     * 输入姓名
     */
    inputStuName: function (e) {
        this.setData({
            stu_name: e.detail.value,
        });
    },

    /**
     * 输入电话
     */
    inputStuTelephone: function (e) {
        this.setData({
            stu_telephone: e.detail.value,
        });
    },

    /**
     * 用户注册
     */
    register: function () {
        const that = this;
        const stu_no = this.data.stu_no.trim();
        const stu_pwd = this.data.stu_pwd.trim();
        const stu_pwd_confirm = this.data.stu_pwd_confirm.trim();
        const stu_name = this.data.stu_name.trim();
        const stu_telephone = this.data.stu_telephone.trim();

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

        if (stu_pwd.length < 6) {
            wx.showToast({
                title: "密码至少6位",
                icon: "none",
            });
            return;
        }

        if (stu_pwd !== stu_pwd_confirm) {
            wx.showToast({
                title: "两次密码不一致",
                icon: "none",
            });
            return;
        }

        if (!stu_name) {
            wx.showToast({
                title: "请输入姓名",
                icon: "none",
            });
            return;
        }

        if (!stu_telephone) {
            wx.showToast({
                title: "请输入电话",
                icon: "none",
            });
            return;
        }

        // 验证手机号格式（简单的11位数字验证）
        if (!/^1[3-9]\d{9}$/.test(stu_telephone)) {
            wx.showToast({
                title: "请输入正确的手机号",
                icon: "none",
            });
            return;
        }

        this.setData({
            loading: true,
        });

        wx.request({
            url: "http://localhost:8080/login/h5_register",
            method: "POST",
            header: {
                "content-type": "application/json",
            },
            data: {
                stu_no: stu_no,
                stu_pwd: stu_pwd,
                stu_name: stu_name,
                stu_telephone: stu_telephone,
            },
            success: function (res) {
                console.log("注册返回:", res.data);
                that.setData({
                    loading: false,
                });

                if (res.data.code === 200) {
                    wx.showToast({
                        title: "注册成功",
                        icon: "success",
                        duration: 2000,
                        success: function () {
                            // 注册成功后自动登录
                            setTimeout(function () {
                                that.autoLogin(stu_no, stu_pwd);
                            }, 2000);
                        },
                    });
                } else {
                    wx.showToast({
                        title: res.data.message || "注册失败",
                        icon: "none",
                    });
                }
            },
            fail: function (error) {
                console.error("注册请求失败:", error);
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
     * 自动登录
     */
    autoLogin: function (stu_no, stu_pwd) {
        const that = this;

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
                if (res.data.code === 200) {
                    const token = res.data.token;
                    const userInfo = res.data.userInfo;

                    // 保存token和用户信息
                    wx.setStorageSync("token", token);
                    wx.setStorageSync("userInfo", userInfo);
                    wx.setStorageSync("stu_no", userInfo.stu_no);

                    // 跳转到选车页面
                    wx.redirectTo({
                        url: "/pages/train_query/train_query",
                    });
                }
            },
        });
    },

    /**
     * 返回登录页面
     */
    goBackToLogin: function () {
        wx.navigateBack();
    },
});